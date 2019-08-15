package de.ironjan.metalonly.streaming

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import de.ironjan.metalonly.MainActivity
import de.ironjan.metalonly.R
import de.ironjan.metalonly.api.model.TrackInfo
import de.ironjan.metalonly.log.LW
import java.text.SimpleDateFormat
import java.util.*

class MoStreamingService : Service() {
    enum class State {
        Gone, Preparing, Started, Completed, Stopping,

        Error
    }

    var lastError: String? = null


    private var stateChangeCallback: StateChangeCallback? = null

    private var state: State = State.Gone


    val isPlayingOrPreparing: Boolean
        get() {
            return state == State.Preparing || state == State.Started
        }

    val canPlay: Boolean
        get() {
            return state == State.Gone
        }

    private lateinit var notificationManager: NotificationManagerCompat

    private fun changeState(newState: State) {
        LW.d(TAG, "Changing state to $newState.")
        state = newState

        stateChangeCallback?.apply {
            onChange(newState)
            LW.d(TAG, "Changing state to $state - Callback invoked.")
        }


        val notification2 = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Metal Only")
                .setContentText("$state") // TODO use show information
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build()
        notificationManager.notify(NOTIFICATION_ID, notification2)
        LW.d(TAG, "Updated notification")

        LW.d(TAG, "Changing state to $state - Completed.")
    }

    val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder? = binder

    var mp: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LW.d(TAG, "handling start command")

        when (intent?.action) {
            ACTION_PLAY -> play()
            ACTION_STOP -> stop()
            else -> LW.d(TAG, "Received unknown action")
        }

        return super.onStartCommand(intent, flags, startId)

    }

    private lateinit var audioManager: AudioManager

    override fun onCreate() {
        super.onCreate()
        LW.d(TAG, "onCreate called")

        notificationManager = NotificationManagerCompat.from(this)
        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager


        pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        val notification2 = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Metal Only")
                .setContentText("Playing stream...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setTicker("TODO Artist - Title") // TODO
                .setOnlyAlertOnce(true)
                .build()


        createNotificationChannel()

        startForeground(NOTIFICATION_ID, notification2)
        LW.d(TAG, "PRomoted service to goreground")

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock")
        LW.d(TAG, "Acquired wifilock")

        LW.d(TAG, "onCreate done")

    }

    private lateinit var wifiLock: WifiManager.WifiLock

    private val CHANNEL_ID = "Metal Only CHANNEL ID" // TODO
    private fun createNotificationChannel() {
        LW.d(TAG, "createNotificationChannel called")

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = (NOTIFICATION_CHANNEL_NAME)
            val descriptionText = ("todo metal only notifications") // TODO
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            LW.d(TAG, "notification channel created")
        } else {
            LW.d(TAG, "api < O. no notification channel necessary")

        }
    }


    private val NOTIFICATION_ID = 0
    private val NOTIFICATION_CHANNEL_NAME = "Metal Only"

    lateinit var pendingIntent: PendingIntent

    lateinit var notification: Notification


    fun play(cb: MoStreamingService.StateChangeCallback) {
        addStateChangeCallback(cb)
        play()
    }

    fun play() {
        LW.d(TAG, "play() called")
        lastError = null

        LW.d(TAG, "Service is in foreground now")

        changeState(State.Preparing)
        mp?.apply {
            release()
            LW.d(TAG, "Released previous media player")
        }


        Thread {
            mp = MediaPlayer()
                    .apply {
                        setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

                        if (Build.VERSION.SDK_INT < 26) {
                            @Suppress("DEPRECATION")
                            setAudioStreamType(AudioManager.STREAM_MUSIC)
                        } else {
                            val b = AudioAttributes.Builder()
                            b.setLegacyStreamType(AudioManager.STREAM_MUSIC)
                            setAudioAttributes(b.build())
                        }
                        setDataSource(streamUri)
                        Log.d(TAG, "Initialized internal media player")


                        setOnErrorListener { mp, what, extra -> onError(what, extra, mp) }
                        setOnCompletionListener { mediaPlayer -> onComplete(mediaPlayer) }
                        setOnBufferingUpdateListener { mp, percent -> bufferingUpdate(percent) }

                        setOnPreparedListener { mediaPlayer -> onPreparedPlay(mediaPlayer) }
                        Log.d(TAG, "Hooked up call backs to internal media player")

                        prepareAsync()
                        Log.d(TAG, "Preparing internal media player async")
                    }
        }.start()
    }

    private fun onError(s: String) {
        val msg = "error: $s"
        LW.e(TAG, msg)
        changeState(State.Error)


        val rightNow = Calendar.getInstance() //initialized with the current date and time
        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(rightNow.time)
        lastError = "$formattedDate: msg"

        LW.d(TAG, "onError(string) called. Triggering stop()")
        stop()

    }
    private fun onError(what: Int, extra: Int, mp: MediaPlayer): Boolean {
        val whatAsSTring = when (what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> "unknown"
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "server died"
            else -> "Undocumented: $what..."
        }
        val extraAsString = when (extra) {
            MediaPlayer.MEDIA_ERROR_IO -> "io"
            MediaPlayer.MEDIA_ERROR_MALFORMED -> "Malformed"
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "unsupported"
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "timeout"
            else -> "undocumented extra: $extra..."
        }

        val msg = "error: $whatAsSTring - $extraAsString"
        LW.e(TAG, msg)
        changeState(State.Error)


        val rightNow = Calendar.getInstance() //initialized with the current date and time
        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(rightNow.time)
        lastError = "$formattedDate: msg"


        LW.d(TAG, "onError(w,e,mp) called. Triggering stop()")
        stop()

        return true
    }


    var continueOnAudioFocusReceived: Boolean = false

    val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        val tag = "MoStreamingService.afChangeListener"

        LW.d(tag, "Received audio focus change to $focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss of audio focus
                // Stop playback immediately
                continueOnAudioFocusReceived = false
                stop()
                LW.d(tag, "Stopped playback, no continue on gain")
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Pause playback
                // we rely on https://developer.android.com/guide/topics/media-apps/audio-focus#automatic-ducking
                // until required otherwise
                mp?.pause()
                continueOnAudioFocusReceived = true
                LW.d(tag, "transient loss. Paused playback, continue on gain")

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower the volume, keep playing todo?
                // we rely on https://developer.android.com/guide/topics/media-apps/audio-focus#automatic-ducking
                // until required otherwise
                LW.d(tag, "transient loss can duck. did nothing")
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
                LW.d(tag, "gained focus, continueOnAudioFocusReceived: $continueOnAudioFocusReceived")

                if(continueOnAudioFocusReceived) {
                    mp?.start()
                    LW.d(tag, "... started playback again")
                }
            }
        }
    }
    private fun onPreparedPlay(mediaPlayer: MediaPlayer) {
        Log.d(TAG, "Preparation complete. Start audio playback")


        val audioFocusRequest = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributesCompat.Builder().run {
                setUsage(AudioAttributesCompat.USAGE_MEDIA)
                setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                // todo add playback delayed?
                setOnAudioFocusChangeListener(afChangeListener, Handler())
                build()
            })
            build()
        }

        val res = AudioManagerCompat.requestAudioFocus(audioManager, audioFocusRequest)

        when (res) {
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> onError("Could not get audio focus (failed). Try again.") // TODO

            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                mediaPlayer.start()
            }
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                onError("Could not get audio focus (delayed). Try again.") // TODO
            }
            else -> onError("Could not get audio focus (unknown). Try again.") // TODO
        }



        changeState(State.Started)
        Log.d(TAG, "Now playing.")
    }

    private fun bufferingUpdate(percent: Int) {
        LW.d(TAG, "Buffering Update: $percent%")
    }

    private fun onComplete(mediaPlayer: MediaPlayer) {
        LW.i(TAG, "onComplete called")
        changeState(State.Completed)
        // todo release?
    }

    fun stop() {
        LW.d(TAG, "stop called")

        changeState(State.Stopping)
        mp?.apply {
            if (state == State.Preparing) {
                this.setOnPreparedListener { mediaPlayer -> stopAndRelease(mediaPlayer) }
                LW.d(TAG, "Set onPreparedListener to stopAndRelease")
            } else {
                stopAndRelease(this)
            }
            LW.d(TAG, "Applied release preparations to media player")
        }
        notificationManager.cancel(NOTIFICATION_ID)
        LW.d(TAG, "Cancelled notification")

        wifiLock.release()
        LW.d(TAG, "Released wifilock")

        LW.d(TAG, "Stopping self...")
        stopSelf()
        LW.d(TAG, "Stopping self... done")
    }

    override fun onDestroy() {
        super.onDestroy()
        LW.d(TAG, "Service is destroyed now.")
    }

    private fun stopAndRelease(mediaPlayer: MediaPlayer) {
        mediaPlayer.stop()
        mediaPlayer.release()
        changeState(State.Gone)
        mp = null
        LW.d(TAG, "Stopped and released mediaplayer")
    }

    fun addStateChangeCallback(cb: StateChangeCallback) {
        stateChangeCallback = cb
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     * See https://developer.android.com/guide/components/bound-services
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): MoStreamingService = this@MoStreamingService

    }

    interface StateChangeCallback {
        fun onChange(newState: State)
        fun onTrackChange(trackInfo: TrackInfo)
    }

    companion object {
        const val ACTION_PLAY = "de.ironjan.metalonly.play"
        const val ACTION_STOP = "de.ironjan.metalonly.stop"
        const val TAG = "MoStreamingService"
        private const val streamUri = "http://server1.blitz-stream.de:4400"
    }
}
package de.ironjan.metalonly.streaming

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

    private var _state: State = State.Gone
    var state: State
        get() = _state
        private set(value) {
            _state = value
        }


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

        LW.d(TAG, "Changing state to $state - Completed.")
    }

    private val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder? = binder

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }
    private var mp: MediaPlayer? = null

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

    private val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private lateinit var myNoisyAudioStreamReceiver: BecomingNoisyReceiver

    override fun onCreate() {
        super.onCreate()
        LW.d(TAG, "onCreate called")

        notificationManager = NotificationManagerCompat.from(this)
        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        myNoisyAudioStreamReceiver = BecomingNoisyReceiver(this)

        pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        val notification2 = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Metal Only")
                .setContentText("Playing stream")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setOnlyAlertOnce(true)
                .build()


        createNotificationChannel()

        startForeground(NOTIFICATION_ID, notification2)
        LW.d(TAG, "Promoted service to foreground")

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock")
        LW.d(TAG, "Acquired wifilock")

        wakeLock =
                (applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                        acquire()
                    }
                }
        LW.d(TAG, "Acquired wakelock explictely.")

        LW.d(TAG, "onCreate done")

    }
private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var wifiLock: WifiManager.WifiLock

    private val CHANNEL_ID = "Metal Only Stream Notifications"

    private fun createNotificationChannel() {
        LW.d(TAG, "createNotificationChannel called")

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = (NOTIFICATION_CHANNEL_NAME)
            val descriptionText = ("Metal Only Stream Notification") // todo move to resource
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(null,null)
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


    private val NOTIFICATION_ID = 1
    private val NOTIFICATION_CHANNEL_NAME = "Metal Only"

    private lateinit var pendingIntent: PendingIntent

    fun play(cb: StateChangeCallback) {
        addStateChangeCallback(cb)
        play()
    }

    private fun play() {
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
                        LW.d(TAG, "Acquired wake lock.")

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
                        setOnBufferingUpdateListener { _, percent -> bufferingUpdate(percent) }
                        setOnInfoListener { _, what, extra -> onMpInfo(what, extra) }

                        setOnPreparedListener { mediaPlayer -> onPreparedPlay(mediaPlayer) }
                        Log.d(TAG, "Hooked up call backs to internal media player")

                        prepareAsync()
                        Log.d(TAG, "Preparing internal media player async")
                    }
        }.start()
    }

    private fun onMpInfo(what: Int, extra:Int): Boolean {
        LW.d(TAG, "MediaPlayer info: $what, $extra")
        return true
    }
    @SuppressLint("SimpleDateFormat")
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

    @SuppressLint("SimpleDateFormat")
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


    private var continueOnAudioFocusReceived: Boolean = false

    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
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
                // Lower the volume, keep playing
                // we rely on https://developer.android.com/guide/topics/media-apps/audio-focus#automatic-ducking
                // until required otherwise
                
                // todo: implement Lower the volume, keep playing?
                LW.d(tag, "transient loss can duck. did nothing")
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
                LW.d(tag, "gained focus, continueOnAudioFocusReceived: $continueOnAudioFocusReceived")

                if (continueOnAudioFocusReceived) {
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
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> onError("Could not get audio focus (failed). Try again.") // TODO move to resource, better message

            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
                LW.d(TAG, "registered myNoisyAudioStreamReceiver")

                mediaPlayer.start()
                LW.d(TAG, "Started playback")
            }
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                onError("Could not get audio focus (delayed). Try again.") // TODO move to resource, better message
            }
            else -> onError("Could not get audio focus (unknown). Try again.") // TODO move to resource, better message
        }



        changeState(State.Started)
        Log.d(TAG, "Now playing.")
    }

    private class BecomingNoisyReceiver(val moStreamingService: MoStreamingService) : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                moStreamingService.stop()
            }
        }
    }


    private fun bufferingUpdate(percent: Int) {
        LW.d(TAG, "Buffering Update: $percent%")
    }

    private fun onComplete(mediaPlayer: MediaPlayer) {
        LW.i(TAG, "onComplete called")
        changeState(State.Completed)
        stop()
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

        wakeLock.release()
        LW.d(TAG, "Released wakelock")

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


        unregisterReceiver(myNoisyAudioStreamReceiver)
        LW.d(TAG, "unregistered noisy audio stream receiver")
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
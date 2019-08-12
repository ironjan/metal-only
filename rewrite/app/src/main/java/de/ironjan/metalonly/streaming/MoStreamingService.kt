package de.ironjan.metalonly.streaming

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.TimedMetaData
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.ironjan.metalonly.MainActivity
import de.ironjan.metalonly.R
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.TrackInfo
import de.ironjan.metalonly.log.LW

class MoStreamingService : Service() {
    enum class State {
        Gone, Preparing, Started, Completed, Stopping,

        Error
    }

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

        when (state) {
            State.Preparing -> {
                notification.tickerText = "Preparing..."
                notificationManager.notify(NOTIFICATION_ID, notification)
            }

            State.Started -> {
                notification.tickerText = "Playing..."
                notificationManager.notify(NOTIFICATION_ID, notification)
            }

            else -> {
                notification.tickerText = "Preparing..."
                notificationManager.cancel(NOTIFICATION_ID)
            }
        }

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

    override fun onCreate() {
        super.onCreate()
        LW.d(TAG, "onCreate called")

        notificationManager = NotificationManagerCompat.from(this)
        pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Metal Only")
                .setContentText("Playing stream...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setTicker("TODO Artist - Title") // TODO
                .build()

        createNotificationChannel()

        LW.d(TAG, "onCreate done")

    }

    private val CHANNEL_ID = "Metal Only CHANNEL ID" // TODO
    private fun createNotificationChannel() {
        LW.d(TAG, "createNotificationChannel called")

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = (NOTIFICATION_CHANNEL_NAME)
            val descriptionText = ("todo metal only notifications") // TODO
            val importance = NotificationManager.IMPORTANCE_DEFAULT
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
        startForeground(NOTIFICATION_ID, notification)

        LW.d(TAG, "Service is in foreground now")

        changeState(State.Preparing)
        mp?.apply {
            release()
            LW.d(TAG, "Released previous media player")
        }

        Thread {
            mp = MediaPlayer()
                    .apply {
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


                        setOnErrorListener { mp, what, extra ->

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

                            LW.e(TAG, "error: $whatAsSTring - $extraAsString")
                            changeState(State.Error)
                            true
                        }
                        setOnCompletionListener { mediaPlayer -> onComplete(mediaPlayer) }
                        setOnBufferingUpdateListener { mp, percent -> bufferingUpdate(percent) }

                        setOnPreparedListener { mediaPlayer -> onPreparedPlay(mediaPlayer) }
                        Log.d(TAG, "Hooked up call backs to internal media player")

                        prepareAsync()
                        Log.d(TAG, "Preparing internal media player async")
                    }
        }.start()
    }


    private fun onInfo(mp: MediaPlayer?, what: Int, extra: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onPreparedPlay(mediaPlayer: MediaPlayer) {
        Log.d(TAG, "Preparation complete. Start audio playback")
        mediaPlayer.start()
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
        LW.d(TAG, "Stopping self...")
        stopSelf()
        LW.d(TAG, "Stopping self... done")
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
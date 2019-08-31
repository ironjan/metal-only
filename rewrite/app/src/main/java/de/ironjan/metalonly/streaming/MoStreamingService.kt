package de.ironjan.metalonly.streaming

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import de.ironjan.metalonly.MainActivity
import de.ironjan.metalonly.R
import de.ironjan.metalonly.log.LW
import java.text.SimpleDateFormat
import java.util.*


/**
 * streaming service. wraps interaction with media player. Use [IStreamingService.Stub] to bind.
 *
 * FIXME handle network change: wifi <-> mobile
 */
class MoStreamingService : Service() {

    private var mp: MediaPlayer? = null
    private var lockHandler: LockHandler? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LW.d(TAG, "Handling start command with action '${intent?.action}'.")

        when (intent?.action) {
            ACTION_PLAY -> play()
            ACTION_STOP -> stop()
            else -> LW.d(TAG, "Received unknown action")
        }

        return super.onStartCommand(intent, flags, startId)

    }

    override fun onCreate() {
        super.onCreate()
        LW.init(this)


        LW.i(TAG, "onCreate called")




        promoteToForeground()

        lockHandler = LockHandler.acquire(this)

        LW.d(TAG, "onCreate done")
    }

    override fun onDestroy() {
        super.onDestroy()
        LW.d(TAG, "Service is destroyed now.")
    }

    // region binding
    override fun onBind(p0: Intent?): IBinder? = binder

    private val binder = StreamingServiceBinder(this)

    // endregion


    // region state handling

    var lastError: String? = null


    private var _state: State = State.Gone
    var state: State
        get() = _state
        private set(value) {
            _state = value
        }

    private var stateChangeCallback: StateChangeCallback? = null


    val isPlayingOrPreparing: Boolean
        get() {
            return state == State.Preparing || state == State.Started
        }

    val canPlay: Boolean
        get() {
            return state == State.Gone
        }


    private fun changeState(newState: State) {
        LW.d(TAG, "Changing state to $newState.")
        state = newState

        stateChangeCallback?.apply {
            onStateChange(newState)
            LW.d(TAG, "Changing state to $state - Callback invoked.")
        }

        LW.d(TAG, "Changing state to $state - Completed.")
    }


    fun addStateChangeCallback(cb: StateChangeCallback) {
        stateChangeCallback = cb
    }
    // endregion


    // region foreground service notification

    private val CHANNEL_ID = "Metal Only Stream Notifications" // todo move to resource?

    private val NOTIFICATION_ID = 1
    private val NOTIFICATION_CHANNEL_NAME = "Metal Only Stream" // todo move to resource?

    private fun promoteToForeground() {
        val pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
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
        LW.i(TAG, "Promoted service to foreground")
    }

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
                setSound(null, null)
            }
            // Register the channel with the system
            NotificationManagerCompat
                .from(this)
                .createNotificationChannel(channel)
            LW.d(TAG, "notification channel created")
        } else {
            LW.d(TAG, "api < O. no notification channel necessary")

        }
    }


    // endregion

    // region play stream
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

        networkObserver = NetworkObserver(this)
        registerReceiver(networkObserver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        networkObserverRegistered = true
        LW.d(TAG, "Registered networkObserver")

        mp = createMediaPlayer("$TAG.play").apply {
            setOnPreparedListener { mediaPlayer -> onPreparedPlay(mediaPlayer) }
            LW.d(TAG, "Hooked up call backs to internal media player")

            prepareAsync()
            LW.d(TAG, "Preparing internal media player async")
        }
    }

    private fun createMediaPlayer(tag: String): MediaPlayer {
        return MediaPlayer()
            .apply {
                setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                LW.d(TAG, "Acquired wake lock.")

                if (Build.VERSION.SDK_INT < 26) {
                    @Suppress("DEPRECATION")
                    (setAudioStreamType(AudioManager.STREAM_MUSIC))
                } else {
                    val b = AudioAttributes.Builder()
                    b.setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    setAudioAttributes(b.build())
                }
                setDataSource(streamUri)
                LW.d(TAG, "Initialized internal media player")


                setOnErrorListener { mp, what, extra -> onError(what, extra, mp) }
                setOnCompletionListener { mediaPlayer -> onComplete(mediaPlayer) }
                setOnBufferingUpdateListener { _, percent -> bufferingUpdate(percent) }
                setOnInfoListener { _, what, extra -> onMpInfo(what, extra) }
            }
    }

    private fun onPreparedPlay(mediaPlayer: MediaPlayer) {
        LW.d(TAG, "Preparation complete. Start audio playback")


        val audioFocusRequest =
            AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributesCompat.Builder().run {
                    setUsage(AudioAttributesCompat.USAGE_MEDIA)
                    setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                    // fixme add playback delayed on transient loss?
                    setOnAudioFocusChangeListener(afChangeListener, Handler())
                    build()
                })
                build()
            }


        val audioManager =
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val res = AudioManagerCompat.requestAudioFocus(audioManager, audioFocusRequest)

        when (res) {
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> onError("Could not get audio focus (failed). Try again.") // TODO move to resource, better message

            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                registerReceiver(
                    myNoisyAudioStreamReceiver,
                    IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                )
                myNoisyAudioStreamReceiverIsRegistered = true
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
        LW.d(TAG, "Now playing.")
    }

    // endregion

    // region mediaplayer callbacks


    private fun onMpInfo(what: Int, extra: Int): Boolean {
        val whatAsString = when (what) {
            MediaPlayer.MEDIA_INFO_UNKNOWN -> "MEDIA_INFO_UNKNOWN"
            MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING -> "MEDIA_INFO_VIDEO_TRACK_LAGGING"
            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> "MEDIA_INFO_VIDEO_RENDERING_START"
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> "MEDIA_INFO_BUFFERING_START" // TODO show "buffering"
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> "MEDIA_INFO_BUFFERING_END" // TODO remove (show "buffering")
            MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING -> "MEDIA_INFO_BAD_INTERLEAVING"
            MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> "MEDIA_INFO_NOT_SEEKABLE"
            MediaPlayer.MEDIA_INFO_METADATA_UPDATE -> "MEDIA_INFO_METADATA_UPDATE"
            MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE -> "MEDIA_INFO_UNSUPPORTED_SUBTITLE"
            MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT -> "MEDIA_INFO_SUBTITLE_TIMED_OUT"

            // Should be MEDIA_INFO_NETWORK_BANDWIDTH but is not available as field..
            703 -> "MEDIA_INFO_NETWORK_BANDWIDTH: bandwidth information is available (as extra kbps)"
            else -> "undocumented what: $what"
        }

//        causeExceptionForCallstack()
        LW.d(TAG, "MediaPlayer info: $whatAsString, $extra")

        return true
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

        causeExceptionForCallstack()

        changeState(State.Error)


        val rightNow = Calendar.getInstance() //initialized with the current date and time
        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(rightNow.time)
        lastError = "$formattedDate: msg"


        LW.e(TAG, "onError($whatAsSTring, $extraAsString, mp) called. Triggering stop()")
        stopAndRelease(mp)

        return true
    }

    private fun causeExceptionForCallstack() {
        val ex = Exception()
        ex.fillInStackTrace()
        val cause = ex.stackTrace
        LW.e(TAG, "Callstack stuff:", ex)
    }


    @SuppressLint("SimpleDateFormat")
    private fun onError(s: String) {
        val msg = "error: $s"
        LW.e(TAG, msg)
        changeState(State.Error)


        val rightNow = Calendar.getInstance() //initialized with the current date and time
        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(rightNow.time)
        lastError = "$formattedDate: msg"

        LW.d(TAG, "onError($s) called. Triggering stopAndRelease() if mp != null")
        LW.e(TAG, s)
        mp?.apply {
            LW.d(TAG, "mp is not null. stopAndRelease")
            stopAndRelease(this)
        }
    }


    private fun bufferingUpdate(percent: Int) {
        LW.d(TAG, "Buffering Update: $percent%")
    }

    private fun onComplete(mediaPlayer: MediaPlayer) {
        // happens when server restarts pr connection is lost. hence restart
        LW.i(TAG, "onComplete called")

        changeState(State.Completed)
        play()
    }
    // endregion

    // region stop related

    fun stopWithCallback(cb: StateChangeCallback) {
        LW.d(TAG, "stop called with callback.")
        stateChangeCallback = cb
        stop()
    }

    fun stop() {
        LW.d(TAG, "stop called")

        LW.d(TAG, "Set isActive to false.")

        changeState(State.Stopping)
        mp?.apply {
            if (state == State.Preparing) {
                this.setOnPreparedListener { mediaPlayer -> stopAndRelease(mediaPlayer) }
                LW.d(TAG, "Set onPreparedListener to stopAndRelease")
//                stopAndRelease(this)
            } else {
                stopAndRelease(this)
            }
            LW.d(TAG, "Applied release preparations to media player")
        }


        cleanUpService()
    }

    private fun cleanUpService() {
        lockHandler?.releaseLocks()

        LW.i(TAG, "Stopping foreground and...")
        stopForeground(true)
        LW.i(TAG, "... stopping self.")
        stopSelf()
        LW.i(TAG, "Stopping foreground and stopping self: done.")
    }


    private fun stopAndRelease(mediaPlayer: MediaPlayer) {
        if (state == State.Started) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        changeState(State.Gone)
        mp = null
        LW.d(TAG, "Stopped and released mediaplayer")


        if (myNoisyAudioStreamReceiverIsRegistered) {
            unregisterReceiver(myNoisyAudioStreamReceiver)
            LW.d(TAG, "unregistered noisy audio stream receiver")
        }

        if (networkObserverRegistered) {
            unregisterReceiver(networkObserver)
            LW.d(TAG, "unregistered networkObserver receiver")
        }
    }
    // endregion


    // region audio focus and noisy receiver
    private val afChangeListener = OnAudioFocusChangeListener(this, this)

    internal fun continuePlayback() {
        mp?.start()
        changeState(State.Started)
    }

    internal fun pause() {
        mp?.pause()
        changeState(State.Paused)
    }

    private val myNoisyAudioStreamReceiver = BecomingNoisyReceiver(this)

    private var myNoisyAudioStreamReceiverIsRegistered = false

    // endregion

    // region network change receiver
    private lateinit var networkObserver: NetworkObserver
    private var networkObserverRegistered = false
    fun restartPlayback() {
        createMediaPlayer("${TAG}.restartPlayback").apply {
            setOnPreparedListener { mediaPlayer ->
                if(state == State.Started) {mp?.stop()}
                mp?.release()
                mp = mediaPlayer
                onPreparedPlay(mediaPlayer)
            }
            LW.d(TAG, "Hooked up call backs to internal media player")

            prepareAsync()
            LW.d(TAG, "Preparing internal media player async")
        }
    }

    // endregion
    companion object {
        const val ACTION_PLAY = "de.ironjan.metalonly.play"
        const val ACTION_STOP = "de.ironjan.metalonly.stop"
        const val TAG = "MoStreamingService"
        private const val streamUri = "http://server1.blitz-stream.de:4400"
    }
}
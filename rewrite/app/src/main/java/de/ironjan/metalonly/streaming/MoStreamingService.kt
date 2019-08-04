package de.ironjan.metalonly.streaming

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import de.ironjan.metalonly.log.LW

class MoStreamingService : Service() {
    enum class State {
        Gone, Initialized, Preparing, Started, Completed,

        Error
    }

    private var stateChangeCallback: StateChangeCallback? = null

    private var state: State = State.Gone


    val isPlayingOrPreparing: Boolean
        get() {return  state == State.Preparing || state == State.Started }

    private fun changeState(newState: State) {
        state = newState
        stateChangeCallback?.onChange(newState)
        LW.d(TAG, "Changed state to $state.")
    }

    val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder? = binder

    var mp: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> play()
            ACTION_STOP -> stop()
            else -> LW.d(TAG, "Received unknown action")
        }


        return super.onStartCommand(intent, flags, startId)

    }

    fun play() {
        changeState(State.Preparing)
        mp?.release()

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
    }

    fun stop() {
        mp?.apply {
            if (state == State.Preparing) {
                this.setOnPreparedListener { mediaPlayer -> stopAndRelease(mediaPlayer) }
            } else {
                stopAndRelease(this)
            }
        }

    }

    private fun stopAndRelease(mediaPlayer: MediaPlayer) {
        mediaPlayer.stop()
        mediaPlayer.release()
        changeState(State.Gone)
        mp = null
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
    }

    companion object {
        const val ACTION_PLAY = "de.ironjan.metalonly.play"
        const val ACTION_STOP = "de.ironjan.metalonly.stop"
        const val TAG = "MoStreamingService"
        private const val streamUri = "http://server1.blitz-stream.de:4400"
    }
}
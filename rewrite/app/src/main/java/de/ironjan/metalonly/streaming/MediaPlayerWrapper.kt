package de.ironjan.metalonly.streaming

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import de.ironjan.metalonly.log.LW


class MediaPlayerWrapper {
    enum class State {
        Gone, Idle, Initialized, Preparing, Prepared, Started, Paused, Completed,

        Error, End
    }

    private var isPrepared: Boolean = false

    private var state: State = State.Gone

    val isPlaying: Boolean
        get() = ::mediaPlayer.isInitialized && mediaPlayer.isPlaying

    val streamUri = "http://server1.blitz-stream.de:4400"

    private val TAG = MediaPlayerWrapper::class.java.canonicalName
    private lateinit var mediaPlayer: MediaPlayer

    init {
        LW.d(TAG, "Creating media player")
        try {

            mediaPlayer = createMediaPlayer()

//        }
            // todo actual error handling
//        catch (e: IOException) {
//            snack(e)
//        } catch (e: IllegalArgumentException) {
//            snack(e)
        } catch (e: Exception) {
            LW.e(TAG, "createmedaiplayer", e)
        }
        LW.d(TAG, "Completed mp create")
    }

    private fun createMediaPlayer(): MediaPlayer {
        val mp = MediaPlayer()
        state = State.Idle

        mp.apply {
            if (Build.VERSION.SDK_INT < 26) {
                @Suppress("DEPRECATION")
                setAudioStreamType(AudioManager.STREAM_MUSIC)
            } else {
                val b = AudioAttributes.Builder()
                b.setLegacyStreamType(AudioManager.STREAM_MUSIC)
                setAudioAttributes(b.build())
            }
            setDataSource(streamUri)
            state = State.Initialized

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
                state = State.Error
                true
            }
            setOnCompletionListener { mediaPlayer -> onComplete(mediaPlayer) }
            setOnBufferingUpdateListener { mp, percent -> bufferingUpdate(percent) }


            setOnPreparedListener { mediaPlayer -> onPrepared(mediaPlayer) }
        }
        return mp
    }

    private fun onPrepared(mediaPlayer: MediaPlayer) {
        LW.d(TAG, "Prepared: $mediaPlayer")
        isPrepared = true
        state = State.Prepared
    }

    private fun bufferingUpdate(percent: Int) {
        LW.d(TAG, "Buffered $percent%")
    }

    private fun onComplete(mediaPlayer: MediaPlayer) {
        LW.d(TAG, "mediaPlayer $mediaPlayer is complete")
        isPrepared = false
        state = State.Completed
    }


    fun play(callBack: MediaPlayerWrapperStartCallback): Boolean {
        if (isPlaying) {
            return true
        }

        if (!::mediaPlayer.isInitialized) {
            LW.w(TAG, "Wait... media player not initialized. Trying again?")
            return false
        }

        if (!isPrepared) {
            LW.d(TAG, "Preparing..")
            callBack.onPrepare()
            mediaPlayer.prepare()
            state = State.Prepared
        }


        mediaPlayer.start()
        callBack.onStarted()
        state = State.Started

        LW.d(TAG, "Started playing")
        return true
    }

    fun stop() {
        mediaPlayer.pause()
        LW.d(TAG, "Stopped playing")
    }

    fun release() {
        if (::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
    }
}
package de.ironjan.metalonly.streaming

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import java.net.HttpCookie


class MediaPlayerWrapper(private val context: Context) {
    private var isPrepared: Boolean = false
    val isPlaying: Boolean
        get() = ::mediaPlayer.isInitialized && mediaPlayer.isPlaying

    val streamUri = "http://server1.blitz-stream.de:4400"

    private val TAG = MediaPlayerWrapper::class.java.canonicalName
    private lateinit var mediaPlayer: MediaPlayer

    init {
        Log.e(TAG, "Creating media player")
        try {

            mediaPlayer = createMediaPlayer()

//        }
            // todo actual error handling
//        catch (e: IOException) {
//            snack(e)
//        } catch (e: IllegalArgumentException) {
//            snack(e)
        } catch (e: Exception) {
            Log.e(TAG, "createmedaiplayer", e)
        }
        Log.d(TAG, "Completed mp create")
    }

    private fun createMediaPlayer(): MediaPlayer {
        val mp = MediaPlayer()
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

                Log.e(TAG, "error: $whatAsSTring - $extraAsString")
                true
            }
            setOnCompletionListener { mediaPlayer -> onComplete(mediaPlayer) }
            setOnBufferingUpdateListener { mp, percent -> bufferingUpdate(percent) }


            setOnPreparedListener { mediaPlayer ->
                onPrepared(mediaPlayer)
            }
        }
        return mp
    }

    private fun onPrepared(mediaPlayer: MediaPlayer) {
        Log.d(TAG, "Prepared: $mediaPlayer")
        isPrepared = true
    }

    private fun bufferingUpdate(percent: Int) {
        Log.d(TAG, "Buffered $percent%")
    }

    private fun onComplete(mediaPlayer: MediaPlayer) {
        Log.d(TAG, "mediaPlayer $mediaPlayer is complete")
        isPrepared = false
    }


    fun play(callBack: MediaPlayerWrapperStartCallback): Boolean {
        if (isPlaying) {
            return true
        }

        if (!::mediaPlayer.isInitialized) {
            Log.w(TAG, "Wait... media player not initialized. Trying again?")
            return false
        }

        if (!isPrepared) {
            Log.d(TAG, "Preparing..")
            callBack.onPrepare()
            mediaPlayer.prepare()
        }


        mediaPlayer.start()
callBack.onStarted()

        Log.d(TAG, "Started playing")
        return true
    }

    fun stop() {
        mediaPlayer.pause()
        Log.d(TAG, "Stopped playing")
    }

    fun release() {
        if (::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
    }
}
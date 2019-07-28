package de.ironjan.metalonly

import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException

// FIXME add actual state handling for mediaplayer
class MainActivity : AppCompatActivity() {


    private var isPlaying: Boolean = false

    private lateinit var action_play: Drawable
    private lateinit var action_stop: Drawable
    private lateinit var mediaPlayer: MediaPlayer
    private var mediaPlayerIsPrepared: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        action_play = ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_play, theme)!!
        action_stop = ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_pause, theme)!!

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            togglePlaying()
        }

        createMediaPlayer()
    }

    private fun togglePlaying() {
        if (isPlaying) {
            stopPlaying()
        } else {
            startPlaying()
        }
    }

    private fun startPlaying() {
        if (!::mediaPlayer.isInitialized) {
            snack("Wait...")
            return
        }
        if (!mediaPlayerIsPrepared) {
            snack("Preparing")
            mediaPlayer.prepare()
        }


        mediaPlayer.start()

        fab.setImageDrawable(action_stop)

        snack("Started playing")

        isPlaying = true
    }

    private fun createMediaPlayer() {
        try {
            val myUri: Uri = Uri.parse("http://server1.blitz-stream.de:4400")

            MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(applicationContext, myUri)
                setOnErrorListener { mp, what, extra ->

                    snack("error: $what - $extra")
                    true
                }
                setOnCompletionListener { mediaPlayerIsPrepared = false }
                setOnBufferingUpdateListener { mp, percent -> bufferingUpdate(percent) }


                setOnPreparedListener {
                    mediaPlayer = it
                    mediaPlayerIsPrepared = true
                    snack("Prepared!")
                }

                snack("Starting prep.")
                prepareAsync()
            }
        }
        // todo actual error handling
        catch (e: IOException) {
            snack(e)
            throw e
        } catch (e: IllegalArgumentException) {
            snack(e)
            throw e
        } catch (e: Exception) {
            snack(e)
            throw e
        }
    }

    private fun bufferingUpdate(percent: Int) {
        bufferingState.text = "$percent% buffered."
    }

    private fun snack(e: Exception) {
        snack(e.message ?: "exception without message")
    }

    private fun stopPlaying() {
        fab.setImageDrawable(action_play)

        mediaPlayer.pause()
        snack("Stopped playing")

        isPlaying = false
    }

    private fun snack(s: String) {
        Snackbar.make(fab, s, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
    }
}
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
import android.util.Log
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.Stats
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


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
        try {
            if (isPlaying) {
                stopPlaying()
            } else {
                startPlaying()
            }
        } catch (e: Exception) {
            snack("Toggle play failed.", e)
        }
    }

    private fun snack(s: String, e: Exception) {
        val em = e.message?:"no exception message"
        snack("$s - $em")
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    private fun loadStats() {
        Thread(Runnable {
            try {
                val stats = Client(this).getStats()

                throw java.lang.Exception("See TODO https://github.com/koush/ion/issues/232")
                if (stats.isLeft()) {
                    stats.mapLeft {
                        snack(it)
                    }
                } else {
                    stats.map {
                        showStats(it)
                    }
                }
            } catch (e: Exception) {
                snack("load stats failed", e)
            }

        }).start()
    }

    private fun showStats(stats: Stats) {
        runOnUiThread {
            val track = stats.track

            txtModerator.text = stats.showInformation.moderator
            txtShow.text = stats.showInformation.show
            txtGenre.text = stats.showInformation.genre
            txtTrack.text = "${track.artist} - ${track.title}"
        }
    }

    private fun startPlaying() {
        if (!::mediaPlayer.isInitialized) {
            snack("Wait... media player not initialized. Trying again?")
            return
        }
        if (!mediaPlayerIsPrepared) {
            snack("Preparing..")
            mediaPlayer.prepare()
        }


        mediaPlayer.start()

        fab.setImageDrawable(action_stop)

        snack("Started playing")

        isPlaying = true
    }

    private fun createMediaPlayer() {
        snack("Creating media player")
        try {
            val myUri: Uri = Uri.parse("http://server1.blitz-stream.de:4400")

            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(applicationContext, myUri)
                setOnErrorListener { mp, what, extra ->
                    snack("error: $what - $extra")
                    true
                }
                setOnCompletionListener { mediaPlayerIsPrepared = false }
                setOnBufferingUpdateListener { mp, percent -> bufferingUpdate(percent) }


                setOnPreparedListener {
                    mediaPlayerIsPrepared = true
                }

                snack("Starting prep.")
                prepareAsync()
                snack("... async")
            }
//        }
        // todo actual error handling
//        catch (e: IOException) {
//            snack(e)
//        } catch (e: IllegalArgumentException) {
//            snack(e)
        } catch (e: Exception) {
            snack("createmedaiplayer" , e)
        }
        snack("Completed mp create")
    }

    private fun showResult(toString: String) {
        runOnUiThread { bufferingState.text = toString }
    }

    private fun bufferingUpdate(percent: Int) {
        showResult("$percent% buffered.")
    }

    private fun stopPlaying() {
        fab.setImageDrawable(action_play)

        mediaPlayer.pause()
        snack("Stopped playing")

        isPlaying = false
    }

    private fun snack(s: String) {
        runOnUiThread {
            Snackbar.make(fab, s, Snackbar.LENGTH_LONG).show()
            val rightNow = Calendar.getInstance() //initialized with the current date and time

            val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(rightNow.time)

            val logString = "$formattedDate: $s\n"

            txtError.text.append(logString)
            Log.d("MainActivity", s)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
    }
}
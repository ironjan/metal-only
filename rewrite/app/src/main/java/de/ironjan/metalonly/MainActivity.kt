package de.ironjan.metalonly

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.koushikdutta.ion.Ion
import de.ironjan.metalonly.log.LW
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.Stats
import de.ironjan.metalonly.streaming.MediaPlayerWrapper
import de.ironjan.metalonly.streaming.MediaPlayerWrapperStartCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


// FIXME add actual state handling for mediaplayer
class MainActivity : AppCompatActivity() {
    private var isResumed: Boolean = false
    private val TAG = "MainActivity"

    private lateinit var action_play: Drawable
    private lateinit var action_stop: Drawable
    private lateinit var stream_loading: Drawable

    private lateinit var mediaPlayerWrapper: MediaPlayerWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        action_play = ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_play, theme)!!
        action_stop = ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_pause, theme)!!
        stream_loading = ResourcesCompat.getDrawable(resources, android.R.drawable.stat_sys_download, theme)!!

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            togglePlaying()
        }

        Client.initIon(this)
        mediaPlayerWrapper = MediaPlayerWrapper()
    }

    private fun togglePlaying() {
        try {
            if (mediaPlayerWrapper.isPlaying) {
                stopPlaying()
            } else {
                startPlaying()
            }
        } catch (e: Exception) {
            LW.e(TAG, "Toggle play failed.", e)
        }
    }

    override fun onResume() {
        super.onResume()
        isResumed = true
        loadStats()
        addLogFetchingThread()
    }

    private fun addLogFetchingThread() {
        LW.d(TAG, "Initializing log view thread.")
        Thread {
            try {
                while (isResumed) {
                    Thread.sleep(5000)
                    val collectedLog = LW.q.joinToString("")
                    runOnUiThread { txtError.text = collectedLog }
                }
            } catch (e: Exception) {
                LW.e(TAG, "Log fetching thread failed.")
            }
        }.start()
        LW.d(TAG, "Initialized log view thread.")
    }

    private fun loadStats() {
        Thread(Runnable {
            try {
                val stats = Client(this).getStats()

                if (stats.isLeft()) {
                    stats.mapLeft {
                        snack(it)
                    }
                } else {
                    stats.map {
                        showStats(it)
                        loadModeratorImage(it)
                    }
                }
            } catch (e: Exception) {
                LW.e(TAG, "Loading stats failed", e)
            }

        }).start()
    }

    private fun loadModeratorImage(stats: Stats) {
        val mod = stats.showInformation.moderator
        val id = resources.getIdentifier(mod.toLowerCase(), "drawable", packageName)
        val modUrl = "https://www.metal-only.de/botcon/mob.php?action=pic&nick=$mod"
        runOnUiThread {
            if (id != 0) {
                imageView.setImageResource(id)
            } else {
                Ion.with(imageView)
                        .placeholder(R.drawable.metalhead)
                        // TODO do we need these?
//                        .error(R.drawable.error_image)
//                        .animateLoad(spinAnimation)
//                        .animateIn(fadeInAnimation)
                        .load(modUrl)

            }
        }
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
        val callBack = object : MediaPlayerWrapperStartCallback {
            override fun onPrepare() {
                runOnUiThread { fab.setImageDrawable(stream_loading) }
            }

            override fun onStarted() {
                runOnUiThread { fab.setImageDrawable(action_stop) }
            }

        }
        Thread {
            if (mediaPlayerWrapper.play(callBack)) {

            } else {
                // FIXME handle and show error, better return type for play or add on error...
                LW.e(TAG, "Playing failed")
                runOnUiThread {
                    fab.setImageDrawable(action_play)
                }
            }
        }.start()

    }

    private fun showResult(toString: String) {
        runOnUiThread { bufferingState.text = toString }
    }

    private fun stopPlaying() {
        mediaPlayerWrapper.stop()
        fab.setImageDrawable(action_play)
        LW.d(TAG, "Stopped playing")
    }

    private fun snack(s: String) {
        runOnUiThread {
            Snackbar.make(fab, s, Snackbar.LENGTH_LONG).show()
        }
    }

    private val STRING_EXTRA_STREAM_EVENT = "STREAM_EVENT"

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val event = intent?.extras?.getString(STRING_EXTRA_STREAM_EVENT)
        if (event != null) {
            LW.w(TAG, event)
            snack(event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerWrapper.release()
    }

    override fun onPause() {
        super.onPause()
        isResumed = false
    }
}
package de.ironjan.metalonly

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.IBinder
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
import de.ironjan.metalonly.streaming.MoStreamingService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


// FIXME add actual state handling for mediaplayer
class MainActivity : AppCompatActivity(), MoStreamingService.StateChangeCallback {
    override fun onChange(newState: MoStreamingService.State) {
        when (newState) {
            MoStreamingService.State.Preparing -> fab.setImageDrawable(stream_loading)
            MoStreamingService.State.Started -> fab.setImageDrawable(action_stop)
            MoStreamingService.State.Gone -> fab.setImageDrawable(action_play)
            MoStreamingService.State.Completed -> snack("on complete")
            MoStreamingService.State.Error -> snack("on error")
        }
    }

    private var isResumed: Boolean = false
    private val TAG = "MainActivity"

    private lateinit var action_play: Drawable
    private lateinit var action_stop: Drawable
    private lateinit var stream_loading: Drawable

    private lateinit var mediaPlayerWrapper: MediaPlayerWrapper

    private lateinit var moStreamingService: MoStreamingService
    private var mBound: Boolean = false

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



    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MoStreamingService.LocalBinder
            moStreamingService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    override fun onStart() {
        super.onStart()
        Intent(this, MoStreamingService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false

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
        moStreamingService.addStateChangeCallback(this)
       moStreamingService.play()

        fab.setImageDrawable(action_play)
        LW.d(TAG, "Stopped playing")
    }


    private fun stopPlaying() {
        moStreamingService.stop()
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
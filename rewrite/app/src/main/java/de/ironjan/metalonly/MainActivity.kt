package de.ironjan.metalonly

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.koushikdutta.ion.Ion
import de.ironjan.metalonly.log.LW
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.Stats
import de.ironjan.metalonly.api.model.TrackInfo
import de.ironjan.metalonly.api.model.ShowInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import de.ironjan.metalonly.streaming.*


class MainActivity : AppCompatActivity(), StateChangeCallback {
    private fun onTrackChange(trackInfo: TrackInfo) {
        val s = "${trackInfo.artist} - ${trackInfo.title}"
        runOnUiThread {
            txtTrack.text = s
        }
        LW.d(TAG, "Track info updated: $s")
    }

    private fun onShowInfoChange(showInfo: ShowInfo) {
        runOnUiThread {
            txtShow.text = showInfo.show
            txtGenre.text = showInfo.genre

            txtAbModerator.text = showInfo.moderator

            txtAbLoading.visibility = View.GONE
            txtAbModerator.visibility = View.VISIBLE
            txtAbIs.visibility = View.VISIBLE
            txtAbOnAir.visibility = View.VISIBLE
        }
    }

    override fun onStateChange(newState: State) {
        LW.d(TAG, "onStateChange($newState) called.")
        when (newState) {
            State.Preparing -> runOnUiThread { fab.setImageDrawable(stream_loading) }
            State.Started -> runOnUiThread { fab.setImageDrawable(action_stop) }
            State.Stopping -> runOnUiThread { fab.setImageDrawable(action_stop) }
            State.Gone -> runOnUiThread { fab.setImageDrawable(action_play) }
            State.Completed -> snack("on complete")
            State.Error -> snack("on error")
        }
        LW.d(TAG, "onStateChange($newState) completed.")
    }

    private var isResumed: Boolean = false
    private val TAG = "MainActivity"

    private lateinit var action_play: Drawable
    private lateinit var action_stop: Drawable
    private lateinit var stream_loading: Drawable
    private lateinit var stopping_drawable: Drawable

    private var mBound: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LW.init(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        action_play = ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_play, theme)!!
        action_stop = ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_pause, theme)!!
        stream_loading = ResourcesCompat.getDrawable(resources, android.R.drawable.stat_sys_download, theme)!!
        stopping_drawable = ResourcesCompat.getDrawable(resources, android.R.drawable.button_onoff_indicator_off, theme)!!

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { togglePlaying() }
        fabMail.setOnClickListener { Mailer.sendFeedback(this) }

        Client.initIon(this)
    }

    private fun togglePlaying() {
        try {
            if (mBound) {
                if (moStreamingService.isPlayingOrPreparing) {
                    moStreamingService.stop()
                    LW.d(TAG, "Stopped playing")
                } else if (moStreamingService.canPlay) {
                    moStreamingService.play(asIStreamChangeCallback())
                    LW.d(TAG, "Started playing")
                }
            } else {
                fab.setImageDrawable(stream_loading)
                Intent(this, MoStreamingService::class.java).also {
                    it.action = MoStreamingService.ACTION_PLAY
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(it)
                        LW.d(TAG, "Running on Android O+, started service as foreground.")
                    } else {
                        startService(it)
                        LW.d(TAG, "Running on Android before O, started service via startService.")
                    }
                    bindService(it, connection, 0)
                }
            }
        } catch (e: Exception) {
            LW.e(TAG, "Toggle play failed.", e)
        }
    }

    override fun onResume() {
        super.onResume()

        isResumed = true

        loadStats()
        Thread {
            val tag = "MainActivity.TrackInfoUpdateThread"
            LW.d(tag, "Prepared track info update thread")
            var lastTrackInfo: TrackInfo? = null
            while (isResumed) {
                Thread.sleep(30 * 1000) // start with sleeping because loadStats includes current track
                // FIXME is this the best way???

                val track = Client(this).getTrack()
                if (track.isRight()) {
                    track.map {
                        if (it != lastTrackInfo) {
                            // broadcast it
                            onTrackChange(it)
                            lastTrackInfo = it
                            LW.d(tag, "'Broadcasted' track info")
                        }
                    }
                }
            }
            LW.d(tag, "Track info update thread is not needed anymore")
        }.start()

        Thread {
            val tag = "MainActivity.ShowInfoUpdateThread"
            LW.d(tag, "Prepared show info update thread")
            var lastShowInfo: ShowInfo? = null
            while (isResumed) {
                Thread.sleep(5 * 60 * 1000) // start with sleeping because loadStats includes current track
                // Replace this with scheduled service or lookup in plan

                val showInfo = Client(this).getShowInfo()
                if (showInfo.isRight()) {
                    showInfo.map {
                        if (it != lastShowInfo) {
                            // broadcast it
                            onShowInfoChange(it)
                            lastShowInfo = it
                            LW.d(tag, "'Broadcasted' show info")
                        }
                    }
                }
            }
            LW.d(tag, "Show info update thread is not needed anymore")
        }.start()

        updateTxtError()

        if (mBound) {
            onStateChange(moStreamingService.state)
        } else {
            Intent(this, MoStreamingService::class.java).also {
                bindService(it, connection, 0)
            }
        }

        checkIgnoreBatteryOptimization()

        LW.d(TAG, "onResume done.")
    }

    private fun checkIgnoreBatteryOptimization() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            LW.d(TAG, "Running on Android < O. No battery optimization request necesary.")
            return
        }

        val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)) {
            LW.d(TAG, "Already ignoring battery optimizations. Request not necessary.")
            return
        }

        // request is needed. FIXME: better way to do this.. *must* include a google-friendly explanation why doze impacts core functionality of the app

        val intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        intent.data = Uri.parse("package:${this.packageName}")
        val activityExists = intent.resolveActivity(packageManager) != null
        if (activityExists) {
            startActivity(intent)
            LW.d(TAG, "Started activity to request whitelisting.")
        }
        else {
            LW.d(TAG, "There is no activity for whitelisting...")
        }

    }

    private lateinit var moStreamingService: IStreamingService

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            moStreamingService = IStreamingService.Stub.asInterface(service)

            moStreamingService.addCallback(asIStreamChangeCallback())

            onStateChange(moStreamingService.state)

            updateTxtError()

            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private fun asIStreamChangeCallback(): IStreamChangeCallback.Stub {
        return object : IStreamChangeCallback.Stub() {
            override fun onNewState(state: State) {
                this@MainActivity.onStateChange(state)
            }
        }
    }

    private fun updateTxtError() {
        runOnUiThread {
            if (mBound && moStreamingService.lastError != null) {
                txtError.text = moStreamingService.lastError ?: ""
            } else {
                txtError.text = ""
            }
        }
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            mBound = false
            unbindService(connection)
        }
    }

    private fun loadStats() {
        Thread(Runnable {
            LW.d(TAG, "Loading stats..")
            try {
                val stats = Client(this).getStats()

                if (stats.isLeft()) {
                    stats.mapLeft {
                        LW.w(TAG, "Loading stats failed: $it")
                        snack(it)
                    }
                } else {
                    stats.map {
                        showStats(it)
                        LW.d(TAG, "Loading stats succeeded. Triggering mod image load.")
                        loadModeratorImage(it)
                    }
                }
            } catch (e: Exception) {
                LW.e(TAG, "Loading stats failed", e)
            }

        }).start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnuWish -> {
                openWish()
                return true
            }
            R.id.mnuPlan -> {
                openPlan()
                return true
            }
            R.id.mnuPDonation -> {
                openDonation()
                return true
            }
            R.id.mnuFeedback -> {
                Mailer.sendFeedback(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openWish() = openBrowser("https://www.metal-only.de/wunschgruss.html")
    private fun openPlan() = openBrowser("https://www.metal-only.de/sendeplan.html")
    private fun openDonation() = openBrowser("https://www.metal-only.de/info-center/donation.html")

    private fun openBrowser(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun loadModeratorImage(stats: Stats) {
        LW.d(TAG, "Starting to load mod image.")
        val mod = stats.showInformation.moderator
        val id = resources.getIdentifier(mod.toLowerCase(), "drawable", packageName)
        val modUrl = "https://www.metal-only.de/botcon/mob.php?action=pic&nick=$mod"
        runOnUiThread {
            if (id != 0) {
                LW.d(TAG, "Mod image delivered via app. Using resource.")
                imageView.setImageResource(id)
            } else {
                LW.d(TAG, "Mod image not delivered via app. Loading from URL.")
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
        LW.d(TAG, "Showing stats")
        val track = stats.track
        val trackAsString = "${track.artist} - ${track.title}"
        val showInformation = stats.showInformation

        runOnUiThread {
            txtShow.text = showInformation.show
            txtGenre.text = showInformation.genre
            txtTrack.text = trackAsString

            txtAbModerator.text = showInformation.moderator

            txtAbLoading.visibility = View.GONE
            txtAbModerator.visibility = View.VISIBLE
            txtAbIs.visibility = View.VISIBLE
            txtAbOnAir.visibility = View.VISIBLE
        }
    }


    private fun snack(s: String) {
        LW.v(TAG, "Called snack($s)")
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
//        Intent(this, MoStreamingService::class.java).also {
//            stopService(it)
//        }
    }

    override fun onPause() {
        super.onPause()
        isResumed = false
        LW.d(TAG, "onPause done")
    }
}
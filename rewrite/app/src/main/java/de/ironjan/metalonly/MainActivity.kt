package de.ironjan.metalonly

import android.content.ComponentName
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
import de.ironjan.metalonly.streaming.*
import kotlinx.android.synthetic.main.action_bar.*

// TODO split out the watcher thread etc -> SRP
class MainActivity : AppCompatActivity(),
    StateChangeCallback,
    MainActivityTrackUpdateThread.TrackUpdate,
    MainActivityShowInfoUpdateThread.OnShowInfoUpdateCallback,
    StatsLoadingRunnable.StatsLoadingCallback {

    // region lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LW.init(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { togglePlaying() }
        fabMail.setOnClickListener { Mailer.sendFeedback(this) }

        Client.initIon(this)

        setContentView(R.layout.activity_main)
        initPlayButtonDrawables()

    }

    override fun onResume() {
        super.onResume()

        isResumed = true

        StatsLoadingRunnable(this).run()
        MainActivityTrackUpdateThread(this).run()
        MainActivityShowInfoUpdateThread(this).run()

        updateTxtError()

        if (mBound) {
            onStateChange(moStreamingService.state)
        } else {
            Intent(this, MoStreamingService::class.java).also {
                it.action = MoStreamingService.ACTION_PLAY
                bindService(it, connection, 0)
                LW.d(TAG, "onResume - binding to service if it exists.")
            }
        }

        LW.d(TAG, "onResume done.")
    }


    override fun onPause() {
        super.onPause()
        isResumed = false
        LW.d(TAG, "onPause done")
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            mBound = false
            unbindService(connection)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBound) {
            unbindService(connection)
            mBound = false
        }

    }

    internal var isResumed: Boolean = false

    // endregion


    // region stats callbacks and mod image loading
    override fun onStatsLoadingError(s: String) = snack(s)

    override fun onStatsLoadingSuccess(stats: Stats) {
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
        LW.d(TAG, "Loading stats succeeded. Triggering mod image load.")
        loadModeratorImage(stats)
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
    // endregion StatsLoadingCallback

    // region track update callbacks
    override fun onTrackChange(trackInfo: TrackInfo) {
        val s = "${trackInfo.artist} - ${trackInfo.title}"
        runOnUiThread {
            txtTrack.text = s
        }
        LW.d(TAG, "Track info updated: $s")
    }
    // endregion

    // region show info update callbackes
    override fun onShowInfoChange(showInfo: ShowInfo) {
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
    // endregion


    // region play button drawables
    private fun initPlayButtonDrawables() {
        val currentTheme = theme
        action_play =
            ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_play, currentTheme)!!
        action_stop = ResourcesCompat.getDrawable(
            resources,
            android.R.drawable.ic_media_pause,
            currentTheme
        )!!
        stream_loading = ResourcesCompat.getDrawable(
            resources,
            android.R.drawable.stat_sys_download,
            currentTheme
        )!!
        stopping_drawable = ResourcesCompat.getDrawable(
            resources,
            android.R.drawable.button_onoff_indicator_off,
            currentTheme
        )!!
    }
    private lateinit var action_play: Drawable
    private lateinit var action_stop: Drawable
    private lateinit var stream_loading: Drawable
    private lateinit var stopping_drawable: Drawable
    // endregion

    // region state handling and state change callback

    override fun onStateChange(newState: State) {
        LW.d(TAG, "onStateChange($newState) called.")
        localState = newState
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

    private var localState: State = State.Gone
    private val localStateIsPlayingOrPreparing
        get() = localState == State.Preparing || localState == State.Started

    // endregion

    // region stream control
    private fun togglePlaying() {
        try {
            if (mBound) {
                if (moStreamingService.isPlayingOrPreparing) {
                    LW.d(TAG, "Service is bound and playing or preparing, directly calling stop().")
                    moStreamingService.stop()
                    LW.d(TAG, "Stopped playing")
                } else if (moStreamingService.canPlay) {
                    LW.d(TAG, "Service is bound and can play, directly calling play(callback).")
                    moStreamingService.play(asIStreamChangeCallback())
                    LW.d(TAG, "Started playing")
                }
            } else if (localStateIsPlayingOrPreparing) {
                LW.d(TAG, "Service is not bound but local state is playing or preparing. Stopping via intent.")
                Intent(this, MoStreamingService::class.java).also {
                    it.action = MoStreamingService.ACTION_STOP

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(it)
                        LW.d(TAG, "Running on Android O+, sending intent as foreground.")
                    } else {
                        startService(it)
                        LW.d(TAG, "Running on Android before O, sending intent via startService.")
                    }
                    fab.setImageDrawable(action_play)
                }
            } else {
                LW.d(
                    TAG,
                    "Service is not bound and local state represents can Play. Starting and binding."
                )
                fab.setImageDrawable(stream_loading)
                startAndBindStreamingService()
            }
        } catch (e: Exception) {
            LW.e(TAG, "Toggle play failed.", e)
        }
    }

    private var mBound: Boolean = false

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

    private fun startAndBindStreamingService() {
        Intent(this, MoStreamingService::class.java).also {
            it.action = MoStreamingService.ACTION_PLAY
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
                LW.d(TAG, "Running on Android O+, started service as foreground.")
            } else {
                startService(it)
                LW.d(TAG, "Running on Android before O, started service via startService.")
            }
            // FIXME leaking service connection
            bindService(it, connection, 0)
        }
    }
    // endregion


    private fun updateTxtError() {
        runOnUiThread {
            if (mBound && moStreamingService.lastError != null) {
                txtError.text = moStreamingService.lastError ?: ""
            } else {
                txtError.text = ""
            }
        }
    }

    // region options menu and navigation
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
    // endregion


    private fun snack(s: String) {
        LW.v(TAG, "Called snack($s)")
        runOnUiThread {
            Snackbar.make(fab, s, Snackbar.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
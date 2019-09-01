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

class MainActivity : AppCompatActivity(){

    // region lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LW.init(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setSupportActionBar(toolbar)

        setContentView(R.layout.activity_main)
        fabMail.setOnClickListener { Mailer.sendFeedback(this) }

        Client.initIon(this)

    }
    // endregion

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


    companion object {
        private const val TAG = "MainActivity"
    }
}
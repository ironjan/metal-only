package de.ironjan.metalonly

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.log.LW
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // region lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LW.init(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        
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
                nav_host_fragment.findNavController().navigate(R.id.action_streamFragment_to_wishFragment2)
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


    fun setModerator(moderator: String?) {
        runOnUiThread{
            if (moderator != null) {
                txtAbModerator?.text = moderator
                txtAbLoading?.visibility = View.GONE
                txtAbModerator?.visibility = View.VISIBLE
                txtAbIs?.visibility = View.VISIBLE
                txtAbOnAir?.visibility = View.VISIBLE
            } else {
                txtAbLoading?.visibility = View.VISIBLE
                txtAbModerator?.visibility = View.GONE
                txtAbIs?.visibility = View.GONE
                txtAbOnAir?.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
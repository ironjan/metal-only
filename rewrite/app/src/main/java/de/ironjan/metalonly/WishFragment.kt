package de.ironjan.metalonly

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.JsonObject
import com.koushikdutta.async.future.FutureCallback
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.Stats
import de.ironjan.metalonly.api.model.Wish
import de.ironjan.metalonly.log.LW
import kotlinx.android.synthetic.main.fragment_wish.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class WishFragment : Fragment(), StatsLoadingRunnable.StatsLoadingCallback {
    override fun onStatsLoadingError(s: String) {
        runOnUiThread { txtExplanation.setText("Konnte Show-Infos nicht laden.") }
    }

    override fun onStatsLoadingSuccess(stats: Stats) {
        val maxNoOfWishesAsString =
            if (stats.maxNoOfWishes == 0) "unbegrenzt" else stats.maxNoOfWishes
        val maxNoOfGreetingsAsString = if (stats.maxNoOfGreetings == 0) "unbegrenzt" else stats
        val playlistFull =
            if (stats.maxNoOfWishesReached) "Sorry, die Playlist ist bereits voll. Es sind keine weiteren Wünsche mehr möglich." else ""

        val msg =
            "Derzeitiges Limit: $maxNoOfWishesAsString Wünsche und $maxNoOfGreetingsAsString Grüße pro Hörer. Wünsche und Grüße sind nur während der moderierten Sendezeit möglich. $playlistFull"

        runOnUiThread {
            editArtist.isEnabled = !stats.maxNoOfWishesReached
            editTitle.isEnabled = !stats.maxNoOfWishesReached
            editGreeting.isEnabled = !stats.maxNoOfGreetingsReached
            txtExplanation.setText(msg)
        }
    }

    private fun runOnUiThread(function: () -> Unit) = activity?.runOnUiThread(function)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnClear.setOnClickListener { clearForm() }
        btnSubmit.setOnClickListener { submit() }

        val lContext = context ?: return
        StatsLoadingRunnable(lContext, this).run()
    }

    private fun clearForm() {
        editArtist.text.clear()
        editTitle.text.clear()
        editGreeting.text.clear()
    }

    private fun submit() {
        val nick = editNick.text.toString()
        val artist = editArtist.text.toString()
        val title = editTitle.text.toString()
        val greeting = editGreeting.text.toString()

        val lContext = context ?: return
        Client(lContext).sendWish(Wish(nick, artist, title, greeting), futureCallback)
    }

    private val futureCallback = object : FutureCallback<String> {
        override fun onCompleted(e: Exception?, result: String?) {
            if (e != null) {
                LW.e(TAG, "Exception when sending wishes.", e)
                snack("Wunsch/Gruß konnte nicht gesendet werden: " + e.message)
                return
            }
            if (result != null) {
                snack("Wunsch/Gruß wurde erfolgreich verschick.")
                clearForm()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(PREF_KEY_NICK, editNick.text.toString())
            putString(PREF_KEY_ARTIST, editArtist.text.toString())
            putString(PREF_KEY_TITLE, editTitle.text.toString())
            putString(PREF_KEY_GREETING, editGreeting.text.toString())
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        editNick.setText(sharedPref.getString(PREF_KEY_NICK, ""))
        editArtist.setText(sharedPref.getString(PREF_KEY_ARTIST, ""))
        editTitle.setText(sharedPref.getString(PREF_KEY_TITLE, ""))
        editGreeting.setText(sharedPref.getString(PREF_KEY_GREETING, ""))
    }

    private fun snack(s: String) {
        LW.v(TAG, "Called snack($s)")

        val context = activity ?: return
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "WishFragment"
        private const val PREF_KEY_NICK = "PREF_KEY_NICK"
        private const val PREF_KEY_ARTIST = "PREF_KEY_WISH"
        private const val PREF_KEY_TITLE = "PREF_KEY_TITLE"
        private const val PREF_KEY_GREETING = "PREF_KEY_GREETING"
    }
}

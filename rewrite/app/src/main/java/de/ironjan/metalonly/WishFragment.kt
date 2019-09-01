package de.ironjan.metalonly

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.api.Api
import com.google.android.material.snackbar.Snackbar
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.log.LW
import kotlinx.android.synthetic.main.fragment_stream.*
import kotlinx.android.synthetic.main.fragment_wish.*

/**
 * A simple [Fragment] subclass.
 */
class WishFragment : Fragment() {

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
    }

    private fun clearForm() {
        editWish.text.clear()
        editGreeting.text.clear()
    }

    private fun submit() {
        val nick = editNick.text.toString()
        val wish = editWish.text.toString()
        val greeting = editGreeting.text.toString()

        snack("Submitted: $nick - $wish -- $greeting")

        clearForm()
    }


    override fun onPause() {
        super.onPause()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(PREF_KEY_NICK, editNick.text.toString())
            putString(PREF_KEY_WISH, editWish.text.toString())
            putString(PREF_KEY_GREETING, editGreeting.text.toString())
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        editNick.setText(sharedPref.getString(PREF_KEY_NICK, ""))
        editWish.setText(sharedPref.getString(PREF_KEY_WISH, ""))
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
        private const val PREF_KEY_WISH = "PREF_KEY_WISH"
        private const val PREF_KEY_GREETING = "PREF_KEY_GREETING"
    }
}

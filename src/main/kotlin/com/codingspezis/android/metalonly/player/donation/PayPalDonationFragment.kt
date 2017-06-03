package com.codingspezis.android.metalonly.player.donation

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.codingspezis.android.metalonly.player.R

class PayPalDonationFragment : Fragment() {

    private var editDonator: EditText? = null
    private var editDonationValue: EditText? = null

    private var btnSend: Button? = null

    private var prefs: SharedPreferences? = null

    private var donator: String? = null

    private var donationValue: Float = 0.toFloat()

    private var actionLabel = "Weiter"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_donation_paypal,
                container, false)

        fetchResources()
        findViews(view)
        bindActions()
        fetchPrefValues()
        bindPrefValues()

        return view
    }

    private fun fetchResources() {
        actionLabel = resources.getString(R.string.donation_toPaypal)
    }

    private fun findViews(view: View) {
        editDonator = view.findViewById(R.id.editDonator) as EditText
        editDonationValue = view
                .findViewById(R.id.editDonationValue) as EditText
        btnSend = view.findViewById(R.id.btnSend) as Button

    }

    private fun bindActions() {
        editDonationValue!!.filters = arrayOf<InputFilter>(CurrencyFormatInputFilter())

        btnSend!!.setOnClickListener { sendDonation() }

        // just setting in xml does not work...
        editDonationValue!!.imeOptions = EditorInfo.IME_ACTION_GO
        editDonationValue!!.setImeActionLabel(actionLabel,
                EditorInfo.IME_ACTION_GO)

        editDonationValue!!
                .setOnEditorActionListener { v, actionId, event ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_NONE,
                            // setting action does nothing..
                            // neither in code nor xml
                        EditorInfo.IME_ACTION_GO -> {
                            sendDonation()
                            false
                        }
                        else -> false
                    }
                }
    }

    private fun fetchPrefValues() {
        prefs = PreferenceManager
                .getDefaultSharedPreferences(activity)
        donator = prefs!!.getString(getString(R.string.paypal_key_sender), "")
        try {
            donationValue = prefs!!.getFloat(
                    getString(R.string.paypal_key_value), -1.0f)
        } catch (e: ClassCastException) {
            fetchDeprecatedDonationValuePref()
        }

    }

    private fun fetchDeprecatedDonationValuePref() {
        // Because this pref was a String in earlier versions
        try {
            val donationValueDeprecated = prefs!!.getString(
                    getString(R.string.paypal_key_value), "-1.0")
            donationValue = java.lang.Float.valueOf(donationValueDeprecated)!!
        } catch (ex: NumberFormatException) {
            donationValue = -1.0f
        }

    }

    private fun bindPrefValues() {
        editDonator!!.setText(donator)
        if (donationValue > 0.0f) {
            editDonationValue!!.setText(donationValue.toString() + "")
        }
    }

    protected fun sendDonation() {
        updateValues()

        if (donationValue <= 0) {
            Toast.makeText(activity,
                    "Der Spendenbetrag kann nicht leer sein.",
                    Toast.LENGTH_LONG).show()
            return
        }

        val paypalURL = PayPalURLGenerator.generatePaypalURL(
                donationValue, donator!!)
        val paypalUri = Uri.parse(paypalURL)
        val paypalIntent = Intent(Intent.ACTION_VIEW, paypalUri)
        startActivity(paypalIntent)
    }

    private fun updateValues() {
        donator = editDonator!!.text.toString()

        try {
            donationValue = java.lang.Float.parseFloat(editDonationValue!!.text
                    .toString())
        } catch (e: NumberFormatException) {
            donationValue = -1.0f
        }

    }

    override fun onPause() {
        updateValues()

        val edit = prefs!!.edit()
        edit.putFloat(getString(R.string.paypal_key_value), donationValue)
        edit.putString(getString(R.string.paypal_key_sender), donator)
        edit.apply()

        super.onPause()
    }

}

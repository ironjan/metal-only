package com.codingspezis.android.metalonly.player.donation

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.text.InputFilter
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.codingspezis.android.metalonly.player.R
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.res.StringRes

@EFragment(R.layout.fragment_donation_paypal)
open class PayPalDonationFragment: Fragment() {

    @JvmField
    @ViewById(R.id.editDonator)
    protected var editDonator: EditText? = null
    @JvmField
    @ViewById(R.id.editDonationValue)
    protected var editDonationValue: EditText? = null

    @JvmField
    @ViewById(R.id.btnSend)
    protected var btnSend: Button? = null

    private var prefs: SharedPreferences? = null

    private var donator: String? = null

    private var donationValue: Float = 0.toFloat()

    @JvmField
    @StringRes(R.string.donation_toPaypal)
    protected var actionLabel: String? = "Weiter"

    @AfterViews
    fun afterViews() {
        bindActions()
        fetchPrefValues()
        bindPrefValues()
    }


    private fun bindActions() {
        editDonationValue!!.filters = arrayOf<InputFilter>(CurrencyFormatInputFilter())

        btnSend!!.setOnClickListener { sendDonation() }

        // just setting in xml does not work...
        editDonationValue!!.imeOptions = EditorInfo.IME_ACTION_GO
        editDonationValue!!.setImeActionLabel(actionLabel,
                EditorInfo.IME_ACTION_GO)

        editDonationValue!!.setOnEditorActionListener { _, actionId, _ ->
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
        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        donator = prefs!!.getString(getString(R.string.paypal_key_sender), "")
        try {
            donationValue = prefs!!.getFloat(getString(R.string.paypal_key_value), -1.0f)
        } catch (e: ClassCastException) {
            fetchDeprecatedDonationValuePref()
        }
    }

    private fun fetchDeprecatedDonationValuePref() {
        // Because this pref was a String in earlier versions
        try {
            val donationValueDeprecated = prefs!!.getString(getString(R.string.paypal_key_value), "-1.0")
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
            Toast.makeText(activity, "Der Spendenbetrag kann nicht leer sein.",
                    Toast.LENGTH_LONG).show()
            return
        }

        val paypalURL = PayPalURLGenerator.generatePaypalURL(donationValue, donator!!)
        val paypalUri = Uri.parse(paypalURL)
        val paypalIntent = Intent(Intent.ACTION_VIEW, paypalUri)
        startActivity(paypalIntent)
    }

    private fun updateValues() {
        donator = editDonator!!.text.toString()

        try {
            donationValue = java.lang.Float.parseFloat(editDonationValue!!.text.toString())
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

package com.codingspezis.android.metalonly.player;

import java.net.*;

import android.content.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * 
 * with this activity users can make paypal donations to metal only
 * 
 */
public class PayPalDonationActivity extends PrefActivity implements
		OnPreferenceChangeListener, OnPreferenceClickListener {

	private Preference prefSender;
	private Preference prefValue;
	private Preference prefSend;
	private Preference prefInfo;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.paypaldonation);

		prefSender = findPreference(getString(R.string.paypal_key_sender));
		prefValue = findPreference(getString(R.string.paypal_key_value));
		prefSend = findPreference(getString(R.string.paypal_key_send));
		prefInfo = findPreference(getString(R.string.paypal_key_info));

		prefSender.setOnPreferenceChangeListener(this);
		prefValue.setOnPreferenceChangeListener(this);
		prefSend.setOnPreferenceClickListener(this);
		prefInfo.setOnPreferenceClickListener(this);

		setSummaries();
	}

	/**
	 * @return value of spender EditTextPreference
	 */
	private String getSender() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String sender = prefs.getString(getString(R.string.paypal_key_sender),
				getString(R.string.paypal_default_sender));
		if (sender.trim().length() == 0) {
			sender = getString(R.string.paypal_default_sender);
		}
		return sender;
	}

	/**
	 * @return value of value EditTextPreference
	 */
	private String getValue() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String value = prefs.getString(getString(R.string.paypal_key_value),
				getString(R.string.paypal_default_value));
		if (value.trim().length() == 0) {
			value = getString(R.string.paypal_default_value);
		}
		return value;
	}

	/**
	 * sets summaries of sender and value EditTextPreference to current value
	 */
	private void setSummaries() {
		prefSender.setSummary(getSender());
		prefValue.setSummary(getValue() + " €");
	}

	/**
	 * sets summary of preference to str
	 * 
	 * @param preference
	 * @param str
	 */
	private void setSummary(Preference preference, String str) {
		if (prefSender == preference && str.trim().length() == 0) {
			str = getString(R.string.paypal_default_sender);
		} else if (prefValue == preference) {
			if (str.trim().length() == 0) {
				str = getString(R.string.paypal_default_value);
			}
			str += " €";
		}
		preference.setSummary(str);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (newValue instanceof String) {
			setSummary(preference, (String) newValue);
		}
		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == prefSend) {
			Uri paypalUri = Uri.parse(generatePaypalURL());
			Intent paypalIntent = new Intent(Intent.ACTION_VIEW, paypalUri);
			startActivity(paypalIntent);
		} else if (preference == prefInfo) {
			Uri metalOnly = Uri
					.parse("http://www.metal-only.de/?action=donation");
			Intent homepage = new Intent(Intent.ACTION_VIEW, metalOnly);
			startActivity(homepage);
		} else {
			return false;
		}
		return true;
	}

	/**
	 * @return PayPal URL for donating entered value
	 */
	private String generatePaypalURL() {

		// this should look like this:
		// Vorname Nachname Spende METAL ONLY e.V.
		// Anonym Spende METAL ONLY e.V.

		String value = getValue().trim();
		// change 12.345.. to 12.34
		if (value.contains(".")) {
			int i = value.indexOf(".");
			if (i + 3 < value.length()) {
				value = value.substring(0, i + 3);
			}
		}

		String returnValue;
		try {
			returnValue = "https://www.paypal.com/cgi-bin/webscr?business="
					+
					// receiver
					"metalonly@gmx.de"
					+ "&cmd=_xclick&currency_code=EUR&amount="
					+
					// value
					URLEncoder.encode(value, "UTF-8")
					+
					// name
					"&item_name=" + URLEncoder.encode(getSender(), "UTF-8")
					+ "%20Spende%20METAL%20ONLY%20e.V.";
		} catch (Exception e) {
			returnValue = "https://www.paypal.com/";
		}
		return returnValue;
	}

}

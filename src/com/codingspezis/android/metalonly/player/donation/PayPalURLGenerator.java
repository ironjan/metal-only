package com.codingspezis.android.metalonly.player.donation;

import java.net.*;

public class PayPalURLGenerator {
	/**
	 * @param donationValue
	 *            how much is donated
	 * @param donator
	 *            who is donating
	 * @return PayPal URL for donating entered value
	 */
	public static String generatePaypalURL(float donationValue, String donator) {

		// this should look like this:
		// Vorname Nachname Spende METAL ONLY e.V.
		// Anonym Spende METAL ONLY e.V.

		final String correctedDonator;
		if (donator.trim().length() == 0) {
			correctedDonator = "ANONYM";
		} else {
			correctedDonator = donator;
		}

		String returnValue;
		try {
			final String encodedDonationValue = URLEncoder.encode(""
					+ donationValue, "UTF-8");
			final String encodedDonator = URLEncoder.encode(correctedDonator,
					"UTF-8");

			returnValue = "https://www.paypal.com/cgi-bin/webscr?business=metalonly@gmx.de&cmd=_xclick&currency_code=EUR&amount="
					+ encodedDonationValue
					+ "&item_name="
					+ encodedDonator
					+ "%20Spende%20METAL%20ONLY%20e.V.";
		} catch (Exception e) {
			returnValue = "https://www.paypal.com/";
		}
		return returnValue;
	}
}

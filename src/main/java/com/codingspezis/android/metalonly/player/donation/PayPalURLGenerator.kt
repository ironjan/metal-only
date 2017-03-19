package com.codingspezis.android.metalonly.player.donation

import java.net.URLEncoder

object PayPalURLGenerator {
    /**
     * @param donationValue how much is donated
     * *
     * @param donator who is donating
     * *
     * @return PayPal URL for donating entered value
     */
    fun generatePaypalURL(donationValue: Float, donator: String): String {

        // this should look like this:
        // Vorname Nachname Spende METAL ONLY e.V.
        // Anonym Spende METAL ONLY e.V.

        val correctedDonator: String
        if (donator.trim().length == 0) {
            correctedDonator = "ANONYM"
        } else {
            correctedDonator = donator
        }

        var returnValue: String
        try {
            val encodedDonationValue = URLEncoder.encode("" + donationValue, "UTF-8")
            val encodedDonator = URLEncoder.encode(correctedDonator,
                    "UTF-8")

            returnValue = "https://www.paypal.com/cgi-bin/webscr?business=metalonly@gmx.de&cmd=_xclick&currency_code=EUR&amount=" +
                    encodedDonationValue +
                    "&item_name=" +
                    encodedDonator +
                    "%20Spende%20METAL%20ONLY%20e.V."
        } catch (e: Exception) {
            returnValue = "https://www.paypal.com/"
        }

        return returnValue
    }
}

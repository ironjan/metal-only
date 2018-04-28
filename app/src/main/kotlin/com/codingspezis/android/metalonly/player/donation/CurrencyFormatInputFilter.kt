package com.codingspezis.android.metalonly.player.donation

import android.text.InputFilter
import android.text.Spanned

import java.util.regex.Pattern

/**
 * Filters EditText input so, that only correct values for money are accepted.
 * http://stackoverflow.com/questions/7627148/edittext-with-currency-format
 */
class CurrencyFormatInputFilter : InputFilter {

    internal var mPattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?")

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {

        val result = dest.subSequence(0, dstart).toString() + source.toString()
        dest.subSequence(dend, dest.length)

        val matcher = mPattern.matcher(result)

        if (!matcher.matches()) {
            return dest.subSequence(dstart, dend)
        }

        return null
    }
}

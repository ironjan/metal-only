package com.codingspezis.android.metalonly.player.donation;

import android.text.*;

import java.util.regex.*;

/**
 * Filters EditText input so, that only correct values for money are accepted.
 * http://stackoverflow.com/questions/7627148/edittext-with-currency-format
 */
public class CurrencyFormatInputFilter implements InputFilter {

	Pattern mPattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?");

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {

		String result = dest.subSequence(0, dstart) + source.toString()
				+ dest.subSequence(dend, dest.length());

		Matcher matcher = mPattern.matcher(result);

		if (!matcher.matches()) {
			return dest.subSequence(dstart, dend);
		}

		return null;
	}
}
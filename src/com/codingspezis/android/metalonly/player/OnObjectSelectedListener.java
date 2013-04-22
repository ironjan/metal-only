package com.codingspezis.android.metalonly.player;

/**
 * OnObjectSelectedListener
 * @version 27.12.2012
 *
 * listener for SpinnerOwnValue
 *
 */
public interface OnObjectSelectedListener {
	
	/**
	 * method is called when some value was selected
	 * @param value value that has been selected
	 */
	public void objectSelected(String value, int index);
	
}

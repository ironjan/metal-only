package com.codingspezis.android.metalonly.player.wish;

import com.codingspezis.android.metalonly.player.wish.WishChecker.*;

/**
 * OnWishesCheckedListener
 * @version 04.01.2013
 *
 * listener for WishChecker and WishCommunicator
 *
 */
public interface OnWishesCheckedListener {

	/**
	 * called when wishes are checked
	 * @param allowedActions result of check
	 */
	public void onWishesChecked(AllowedActions allowedActions);
	
}

package com.codingspezis.android.metalonly.player;

import com.codingspezis.android.metalonly.player.WishChecker.AllowedActions;

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

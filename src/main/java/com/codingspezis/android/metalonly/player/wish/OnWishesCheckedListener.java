package com.codingspezis.android.metalonly.player.wish;

/**
 * listener for WishChecker and WishCommunicator
 */
public interface OnWishesCheckedListener {

    /**
     * called when wishes are checked
     *
     * @param allowedActions result of check
     */
    public void onWishesChecked(AllowedActions allowedActions);

}

package com.codingspezis.android.metalonly.player.stream;

/**
 * Created by r on 19.09.14.
 */
public interface OnTimeoutListener {

    /**
     * called when the stream was not played for STREAK_LIMIT * CHECK_INTERVAL milliseconds
     */
    public void onTimeout();

}

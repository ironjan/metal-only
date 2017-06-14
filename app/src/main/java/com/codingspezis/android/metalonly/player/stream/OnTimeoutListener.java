package com.codingspezis.android.metalonly.player.stream;

public interface OnTimeoutListener {

    /**
     * called when the stream was not played for STREAK_LIMIT * CHECK_INTERVAL milliseconds
     */
    void onTimeout();

}

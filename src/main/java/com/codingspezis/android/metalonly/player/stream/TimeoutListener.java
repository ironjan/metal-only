package com.codingspezis.android.metalonly.player.stream;

/**
 * Created by r on 18.09.14.
 */
public class TimeoutListener implements Runnable {

    private static final int CHECK_INTERVAL = 1 * 1000; // 1 sec
    private static final int STREAK_LIMIT = 5;
    private boolean active;
    private int stopStreak;
    private OnTimeoutListener onTimeoutListener;

    /**
     * checks the player every CHECK_INTERVAL milliseconds
     */
    public void run() {
        stopStreak = 0;
        active = true;
        while (active) {
            checkStream();
            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                // everything is fine
            }
        }
    }

    /**
     * stops the timeout listener
     */
    public void stop() {
        active = false;
    }

    /**
     * checks if the player is still running
     */
    private void checkStream() {
        if (StreamPlayerInternal.IsPlaying()) {
            if (stopStreak != 0)
                stopStreak = 0;
        } else {
            if (++stopStreak >= STREAK_LIMIT) {
                if (onTimeoutListener != null)
                    onTimeoutListener.onTimeout();
                active = false;
            }
        }
    }

    /**
     * sets the OnTimeoutListener to get a signal when the stream timed out
     *
     * @param onTimeoutListener OnTimeoutListener to set
     */
    public void setOnTimeoutListener(OnTimeoutListener onTimeoutListener) {
        this.onTimeoutListener = onTimeoutListener;
    }
}

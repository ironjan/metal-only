package com.codingspezis.android.metalonly.player.siteparser;

import java.io.BufferedReader;

/**
 * this interface can handle {@link HTTPGrabber} events
 */
public interface OnHTTPGrabberListener {

    /**
     * this is called when HTTP GET was successful
     *
     * @param httpResponse BufferedReader reading GET response (doesn't has to be closed)
     */
    void onSuccess(BufferedReader httpResponse);

    /**
     * this is called when HTTP GET was not successful until 5 seconds after
     * start
     */
    void onTimeout();

    /**
     * this is called when user cancels progress dialog
     */
    void onCancel();

    /**
     * this is called when an error occurred while sending and receiving
     *
     * @param error error message
     */
    void onError(String error);

}

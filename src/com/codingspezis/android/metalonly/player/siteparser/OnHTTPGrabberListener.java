package com.codingspezis.android.metalonly.player.siteparser;

import java.io.*;

/**
 * 
 * this interface can handle {@link HTTPGrabber} events
 * 
 * @author codingspezis.com
 * 
 */
public interface OnHTTPGrabberListener {

	/**
	 * this is called when HTTP GET was successful
	 * 
	 * @param httpResponse
	 *            BufferedReader reading GET response (doesn't has to be closed)
	 */
	public void onSuccess(BufferedReader httpResponse);

	/**
	 * this is called when HTTP GET was not successful until 5 seconds after
	 * start
	 */
	public void onTimeout();

	/**
	 * this is called when user cancels progress dialog
	 */
	public void onCancel();

	/**
	 * this is called when an error occurred while sending and receiving
	 * 
	 * @param error
	 *            error message
	 */
	public void onError(String error);

}

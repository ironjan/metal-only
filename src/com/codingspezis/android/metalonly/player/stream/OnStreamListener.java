package com.codingspezis.android.metalonly.player.stream;

/**
 * OnMetadataListener
 * @version 09.01.2013
 *
 * listener for MusicStream
 *
 */
public interface OnStreamListener {

	public void streamConnected();
	public void metadataReceived(String data);
	public void errorOccurred(String err, boolean canPlay);
	public void streamTimeout();
	
}

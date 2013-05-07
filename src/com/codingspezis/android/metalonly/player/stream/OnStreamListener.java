package com.codingspezis.android.metalonly.player.stream;

/**
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

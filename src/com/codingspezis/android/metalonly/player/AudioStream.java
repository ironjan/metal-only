package com.codingspezis.android.metalonly.player;

/**
 * 
 * <b>AudioStream</b><br/>
 * 
 * this class was used to handle different stream decoders in older versions<br/>
 * (can be useful in future)
 * 
 * @author codingspezis.com
 * @version Apr 2, 2013
 *
 */
public interface AudioStream {
		
	/**
	 * starts decoding and playing stream
	 */
	public void startPlaying();
	
	/**
	 * stops decoding and playing stream
	 */
	public void stopPlaying();
	
	/**
	 * @return true if decoding and playing is still running
	 */
	public boolean isPlaying();
	
	/**
	 * TODO: put this method to an other class<br/>
	 * sets listener for handle meta data etc.
	 * @param streamListener listener to set
	 */
	public void setOnStreamListener(OnStreamListener streamListener);
	
}

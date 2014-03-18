package com.codingspezis.android.metalonly.player.stream;

/**
 * this class was used to handle different stream decoders in older versions<br/>
 * (can be useful in future)
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
     * TODO: put this method to an other class
     * sets listener for handle meta data etc.
     *
     * @param streamListener listener to set
     */
    public void setOnStreamListener(OnStreamListener streamListener);

}

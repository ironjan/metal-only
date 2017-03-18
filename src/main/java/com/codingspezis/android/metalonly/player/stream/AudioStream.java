package com.codingspezis.android.metalonly.player.stream;

/**
 * this class was used to handle different stream decoders in older versions<br/>
 * (can be useful in future)
 */
public interface AudioStream {

    /**
     * starts decoding and playing stream
     */
    void startPlaying();

    /**
     * stops decoding and playing stream
     */
    void stopPlaying();

    /**
     * @return true if decoding and playing is still running
     */
    boolean isPlaying();

    /**
     * TODO: put this method to an other class
     * sets listener for handle meta data etc.
     *
     * @param streamListener listener to set
     */
    void setOnStreamListener(OnStreamListener streamListener);

}

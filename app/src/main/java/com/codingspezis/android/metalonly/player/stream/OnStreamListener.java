package com.codingspezis.android.metalonly.player.stream;

/**
 * listener for MusicStream
 */
public interface OnStreamListener {

    /**
     * Called, when the stream connected
     */
    void streamConnected();

    /**
     * Called, when metadata is received
     *
     * @param data The received metadata
     */
    void metadataReceived(String data);

    /**
     * Called when an error occured
     *
     * @param err     a error descirption
     * @param canPlay true, if the stream can keep playing
     */
    void errorOccurred(String err, boolean canPlay);

    /**
     * Called, when the stream timed out
     */
    void streamTimeout();

}

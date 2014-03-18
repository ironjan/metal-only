package com.codingspezis.android.metalonly.player.stream;

/**
 * listener for MusicStream
 */
public interface OnStreamListener {

    /**
     * Called, when the stream connected
     */
    public void streamConnected();

    /**
     * Called, when metadata is received
     *
     * @param data The received metadata
     */
    public void metadataReceived(String data);

    /**
     * Called when an error occured
     *
     * @param err     a error descirption
     * @param canPlay true, if the stream can keep playing
     */
    public void errorOccurred(String err, boolean canPlay);

    /**
     * Called, when the stream timed out
     */
    public void streamTimeout();

}

package com.codingspezis.android.metalonly.player.stream.metadata;

public interface OnMetadataReceivedListener {

    /**
     * called whenever metadata gets available
     *
     * @param metadata matadata as string
     */
    void onMetadataReceived(String metadata);

    /**
     * called whenever an error occurred during receiving metadata
     *
     * @param exception occurred exception
     */
    void onMetadataError(Exception exception);

}

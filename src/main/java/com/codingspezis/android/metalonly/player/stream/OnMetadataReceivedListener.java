package com.codingspezis.android.metalonly.player.stream;

/**
 * Created by r on 17.09.14.
 */
public interface OnMetadataReceivedListener {

    /**
     * called whenever metadata gets available
     * @param metadata
     *      mata data as string
     */
    public void onMetadataReceived(String metadata);

    /**
     * called whenever an error occurred during receiving meta data
     * @param exception
     *      occurred exception
     */
    public void onMetadataError(Exception exception);

}

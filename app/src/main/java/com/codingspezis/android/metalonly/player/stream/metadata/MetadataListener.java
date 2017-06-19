package com.codingspezis.android.metalonly.player.stream.metadata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MetadataListener implements Runnable {

    private static final int REFRESH_INTERVAL = 20 * 1000;
    private IcyStreamMeta icyStreamMeta;
    private OnMetadataReceivedListener onMetadataReceivedListener;
    private boolean active;
    private boolean err;

    /**
     * constructor
     *
     * @param streamUrl
     */
    public MetadataListener(String streamUrl) {
        err = false;
        try {
            icyStreamMeta = new IcyStreamMeta(REFRESH_INTERVAL, new URL(streamUrl));
        } catch (MalformedURLException e) {
            // this should never happen
            err = true;
        }
    }

    /**
     * sets the onMetadataReceivedListener
     *
     * @param onMetadataReceivedListener listener to set
     */
    public void setOnMetadataReceivedListener(OnMetadataReceivedListener onMetadataReceivedListener) {
        this.onMetadataReceivedListener = onMetadataReceivedListener;
    }

    /**
     * stops the meta data receiver
     */
    public void stop() {
        active = false;
    }

    /**
     * gets meta data every REFRESH_INTERVAL milliseconds
     */
    @Override
    public void run() {
        active = true;
        String metadata;
        while (active && !err) {
            try {
                icyStreamMeta.refreshMeta();
                metadata = icyStreamMeta.getStreamTitle();
                if (metadata.trim().length() != 0) {
                    if (onMetadataReceivedListener != null && active)
                        onMetadataReceivedListener.onMetadataReceived(metadata);
                }
            } catch (IOException e) {
                if (onMetadataReceivedListener != null && active)
                    onMetadataReceivedListener.onMetadataError(e);
            }
            try {
                Thread.sleep(REFRESH_INTERVAL);
            } catch (InterruptedException e) {
                // everything is fine
            }
        }
    }
}

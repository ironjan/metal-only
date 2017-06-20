package com.codingspezis.android.metalonly.player.stream.metadata;

import android.content.Context;
import android.content.Intent;

import com.codingspezis.android.metalonly.player.BuildConfig;
import com.codingspezis.android.metalonly.player.core.Track;
import com.codingspezis.android.metalonly.player.stream.track_info.IntentConstants;
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPIWrapper;
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPIWrapper_;
import com.codingspezis.android.metalonly.player.utils.jsonapi.NoInternetException;
import com.codingspezis.android.metalonly.player.utils.jsonapi.TrackWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

public class MetadataListener implements Runnable {

    private static final int _15_MIN_IN_MILLIS = 15 * 1000;
    private static final int UPDATE_INTERVAL = (BuildConfig.DEBUG) ? 5000 : _15_MIN_IN_MILLIS;
    private final Logger LOGGER = LoggerFactory.getLogger(MetadataListener.class);
    private final MetalOnlyAPIWrapper apiWrapper;
    private OnMetadataReceivedListener onMetadataReceivedListener;
    private boolean active;
    private boolean err;
    private final Context context;

    /**
     * constructor
     *
     */
    public MetadataListener(Context context) {
        this.context = context;
        err = false;
        this.apiWrapper = MetalOnlyAPIWrapper_.getInstance_(context);
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
     * gets meta data every 15 seconds
     */
    @Override
    public void run() {
        active = true;
        String metadata;
        while (active && !err) {
            try {
                TrackWrapper trackWrapper = apiWrapper.getTrack();

                if(trackWrapper != null){
                    final Track track = trackWrapper.getTrack();
                    Intent intent = new Intent(IntentConstants.INTENT_NEW_TRACK);
                    intent.putExtra(IntentConstants.KEY_ARTIST, track.getArtist());
                    intent.putExtra(IntentConstants.KEY_TITLE, track.getTitle());
                    context.sendBroadcast(intent);
                }
            } catch (RestClientException e) {
                /** FIXME handle this */
                LOGGER.error(e.getMessage(), e);
            } catch (NoInternetException e) {
                /** FIXME handle this */
                LOGGER.error(e.getMessage(), e);
            }
            try {
                Thread.sleep(UPDATE_INTERVAL);
            } catch (InterruptedException e) {
                // everything is fine
            }
        }
    }
}

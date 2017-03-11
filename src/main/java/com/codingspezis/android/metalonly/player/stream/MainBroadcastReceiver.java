package com.codingspezis.android.metalonly.player.stream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codingspezis.android.metalonly.player.BuildConfig;
import com.codingspezis.android.metalonly.player.StreamControlActivity;
import com.codingspezis.android.metalonly.player.stream.metadata.Metadata;

/**
 * broadcast receiver class for communication between other activities or
 * services
 */
public class MainBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = MainBroadcastReceiver.class.getSimpleName();
    private final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TAG);

    private final StreamControlActivity streamControlActivity;

    /**
     * @param streamControlActivity
     */
    public MainBroadcastReceiver(StreamControlActivity streamControlActivity) {
        this.streamControlActivity = streamControlActivity;
        if (BuildConfig.DEBUG)
            LOGGER.debug("MainBroadcastReceiver({}) constructed", streamControlActivity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) LOGGER.debug("onReceive({},{})", context, intent);

        String metadata = intent
                .getStringExtra(PlayerService.BROADCAST_EXTRA_META);

        // is playing?
        if (intent.getAction().equals(PlayerService.INTENT_STATUS)) {
            this.streamControlActivity
                    .setSupportProgressBarIndeterminateVisibility(false);
            if (intent.getBooleanExtra(PlayerService.BROADCAST_EXTRA_CONNECTED, false)) {
                this.streamControlActivity.setShouldPlay(true);
                this.streamControlActivity.toggleStreamButton(true);
                this.streamControlActivity.setMetadata(Metadata.fromString(metadata));
                this.streamControlActivity.displayMetadata();
            } else {
                this.streamControlActivity.toggleStreamButton(false);
            }
            // meta data
        } else if (intent.getAction().equals(PlayerService.INTENT_METADATA)) {
            this.streamControlActivity.setMetadata(Metadata.fromString(metadata));
            this.streamControlActivity.refreshShowInfo();
            this.streamControlActivity.displaySongs();
        }

        if (BuildConfig.DEBUG) LOGGER.debug("onReceive({},{}) done", context, intent);

    }
}
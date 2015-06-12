package com.codingspezis.android.metalonly.player.stream;

import android.content.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.stream.metadata.Metadata;

/**
 * broadcast receiver class for communication between other activities or
 * services
 */
public class MainBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = MainBroadcastReceiver.class.getSimpleName();
    private final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TAG);

    private final MainActivity mainActivity;

    /**
     * @param mainActivity
     */
    public MainBroadcastReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        if (BuildConfig.DEBUG) LOGGER.debug("MainBroadcastReceiver({}) constructed", mainActivity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) LOGGER.debug("onReceive({},{})", context, intent);

        // is playing?
        if (intent.getAction().equals(PlayerService.INTENT_STATUS)) {
            this.mainActivity
                    .setSupportProgressBarIndeterminateVisibility(false);
            if (intent.getBooleanExtra(PlayerService.BROADCAST_EXTRA_CONNECTED, false)) {
                this.mainActivity.setShouldPlay(true);
                this.mainActivity.toggleStreamButton(true);
                this.mainActivity.setMetadata(Metadata.fromString(intent
                        .getStringExtra(PlayerService.BROADCAST_EXTRA_META)));
                this.mainActivity.displayMetadata();
            } else {
                this.mainActivity.toggleStreamButton(false);
            }
            // meta data
        } else if (intent.getAction().equals(PlayerService.INTENT_METADATA)) {
            String metadata = intent.getStringExtra(PlayerService.BROADCAST_EXTRA_META);
            this.mainActivity.setMetadata(Metadata.fromString(metadata));
            this.mainActivity.refreshShowInfo();
            this.mainActivity.displaySongs();
        }

        if (BuildConfig.DEBUG) LOGGER.debug("onReceive({},{}) done", context, intent);

    }
}
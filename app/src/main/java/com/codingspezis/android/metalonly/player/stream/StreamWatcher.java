package com.codingspezis.android.metalonly.player.stream;

import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

/**
 * listener for stream (e.g. meta data)
 */
class StreamWatcher implements OnStreamListener {

    private final PlayerService playerService;
    private String metadata;

    /**
     * @param playerService
     */
    StreamWatcher(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public void metadataReceived(String data) {
        if (!data.equals(metadata) && data.trim().length() != 0) {
            // here we have new meta data
            metadata = data;
            this.playerService.addSongToHistory(metadata);
            this.playerService.playerService.notify(metadata);

            Intent metaIntent = new Intent(PlayerService.INTENT_METADATA);
            metaIntent.putExtra(PlayerService.BROADCAST_EXTRA_META, data);
            this.playerService.sendBroadcast(metaIntent);
        }
    }

    public String getMetadata() {
        return metadata;
    }

    public void deleteMetadata() {
        metadata = null;
    }

    @Override
    public void errorOccurred(final String err, final boolean canPlay) {
        (new Handler(this.playerService.playerService.getMainLooper()))
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                StreamWatcher.this.playerService.playerService,
                                err, Toast.LENGTH_LONG).show();
                        if (!canPlay) {
                            Intent tmpIntent = new Intent(PlayerService.INTENT_STOP);
                            StreamWatcher.this.playerService.sendBroadcast(tmpIntent);
                        }
                    }
                });
    }

    @Override
    public void streamTimeout() {
        this.playerService.clear(); // TODO: clean clean method

		/*
         * TODO: make som noise here
		 * 
		 * Intent tmpIntent = new Intent(timeoutIntendAction);
		 * sendBroadcast(tmpIntent);
		 */
    }

    @Override
    public void streamConnected() {
        this.playerService.sendPlayerStatus();
    }

}
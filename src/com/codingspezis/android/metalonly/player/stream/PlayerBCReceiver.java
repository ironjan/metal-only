package com.codingspezis.android.metalonly.player.stream;

import com.codingspezis.android.metalonly.player.stream.*;

import android.content.*;

/**
 * PlayerBCReceiver
 * 
 * @version 25.12.2012
 * 
 *          broadcast receiver that handles messages for this service
 * 
 */
public class PlayerBCReceiver extends BroadcastReceiver {

	/**
	 * 
	 */
	private final PlayerService playerService;

	/**
	 * @param playerService
	 */
	PlayerBCReceiver(PlayerService playerService) {
		this.playerService = playerService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// start stream
		if (intent.getAction().equals(PlayerService.INTENT_PLAY)) {
			if (this.playerService.audioStream != null) {
				this.playerService.audioStream.stopPlaying();
			}
			this.playerService.instantiateSelectedPlayer();
			this.playerService.streamPlaying = true;
			this.playerService.audioStream.setOnStreamListener(this.playerService.streamWatcher);
			this.playerService.audioStream.startPlaying();
		}
		// stop stream
		else if (intent.getAction().equals(PlayerService.INTENT_STOP)) {
			if (this.playerService.audioStream != null) {
				this.playerService.audioStream.stopPlaying();
			}
			this.playerService.clear();
		}
		// playing request
		else if (intent.getAction().equals(PlayerService.INTENT_STATUS_REQUEST)) {
			this.playerService.sendPlayerStatus();
		}
		// exit
		else if (intent.getAction().equals(PlayerService.INTENT_EXIT)) {
			if (!this.playerService.streamPlaying) {
				this.playerService.stopSelf();
			}
		}
	}

}
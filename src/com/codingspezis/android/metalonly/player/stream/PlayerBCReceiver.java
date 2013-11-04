package com.codingspezis.android.metalonly.player.stream;

import android.content.*;
import android.media.*;
import android.media.AudioManager.OnAudioFocusChangeListener;

/**
 * 
 * broadcast receiver that handles messages for this service
 * 
 */
public class PlayerBCReceiver extends BroadcastReceiver {

	/**
	 * 
	 */
	private final PlayerService playerService;

	private AudioManager audioManager;

	/**
	 * @param playerService
	 */
	PlayerBCReceiver(PlayerService playerService) {
		this.playerService = playerService;
		audioManager = (AudioManager) playerService
				.getSystemService(Context.AUDIO_SERVICE);
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(PlayerService.INTENT_PLAY)) {
			play();
		}
		else if (intent.getAction().equals(PlayerService.INTENT_STOP)) {
			stop();
		}
		else if (intent.getAction().equals(PlayerService.INTENT_STATUS_REQUEST)) {
			sendPlayerStatus();
		}
		else if (intent.getAction().equals(PlayerService.INTENT_EXIT)) {
			exit();
		}
	}

	private void play() {
		if (playerService.audioStream != null) {
			playerService.audioStream.stopPlaying();
		}

		playerService.instantiateSelectedPlayer();
		playerService.streamPlaying = true;
		playerService.audioStream
				.setOnStreamListener(playerService.streamWatcher);

		playerService.audioStream.startPlaying();
	}

	private void stop() {
		if (playerService.audioStream != null) {
			playerService.audioStream.stopPlaying();
		}
		playerService.clear();
	}

	private void sendPlayerStatus() {
		playerService.sendPlayerStatus();
	}

	private void exit() {
		if (!playerService.streamPlaying) {
			playerService.stopSelf();
		}
	}

}
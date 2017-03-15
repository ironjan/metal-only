package com.codingspezis.android.metalonly.player.stream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.util.Log;

import com.codingspezis.android.metalonly.player.BuildConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * broadcast receiver that handles messages for this service
 */
public class PlayerBCReceiver extends BroadcastReceiver {

    private static final String TAG = PlayerBCReceiver.class.getSimpleName();
    private final Logger LOGGER = LoggerFactory.getLogger(TAG);

    private final PlayerService playerService;

    AudioManager audioManager;

    private OnAudioFocusChangeListener afChangeListener;
    private boolean mPaused = false;

    /**
     * @param playerService
     */
    PlayerBCReceiver(PlayerService playerService) {
        this.playerService = playerService;
        audioManager = (AudioManager) playerService
                .getSystemService(Context.AUDIO_SERVICE);

        afChangeListener = new OnAudioFocusChangeListener() {

            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    if (BuildConfig.DEBUG) LOGGER.debug("!AUDIOFOCUS_LOSS_TRANSIENT");
                    pause();
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    if (BuildConfig.DEBUG) LOGGER.debug("!AUDIOFOCUS_GAIN");
                    continueAfterPause();
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    if (BuildConfig.DEBUG) LOGGER.debug("!AUDIOFOCUS_LOSS");
                    audioManager.abandonAudioFocus(this);
                    stop();
                }
            }
        };

        if (BuildConfig.DEBUG) LOGGER.debug("PlayerBCReceiver({}) created", playerService);
    }

    private void continueAfterPause() {
        if (BuildConfig.DEBUG) LOGGER.debug("pauseEnd()");

        if (mPaused) {
            play();
            mPaused = false;
        }

        if (BuildConfig.DEBUG) LOGGER.debug("pauseEnd() done");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) LOGGER.debug("onReceive({},{})", context, intent);

        String action = intent.getAction();

        Log.d("PayerBCReceiver", "received intent: " + action);

        switch (action) {
            case PlayerService.INTENT_PLAY:
                play();
                sendPlayerStatus();
                break;
            case PlayerService.INTENT_STOP:
                stop();
                sendPlayerStatus();
                break;
            case PlayerService.INTENT_STATUS_REQUEST:
                sendPlayerStatus();
                break;
            case PlayerService.INTENT_EXIT:
                exit();
                break;
            default:
                /* ignore unknown case */
                break;
        }

        if (BuildConfig.DEBUG) LOGGER.debug("onReceive({},{}) done", context, intent);
    }

    void play() {
        if (BuildConfig.DEBUG) LOGGER.debug("play()");
        stop();

        int result = audioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result) {
            playerService.streamPlaying = true;
            playerService.audioStream.setOnStreamListener(playerService.streamWatcher);

            playerService.setForeground();
            playerService.audioStream.startPlaying();
        } else {
            stop();
        }
        if (BuildConfig.DEBUG) LOGGER.debug("play() done");
    }

    void stop() {
        if (BuildConfig.DEBUG) LOGGER.debug("stop()");
        if (playerService.audioStream != null) {
            playerService.audioStream.stopPlaying();
        }
        playerService.clear();
        if (BuildConfig.DEBUG) LOGGER.debug("stop() done");
    }

    void pause() {
        if (BuildConfig.DEBUG) LOGGER.debug("pause()");

        if (playerService.streamPlaying) {
            mPaused = true;
            stop();
        }

        playerService.clear();
        if (BuildConfig.DEBUG) LOGGER.debug("pause() done");
    }


    private void sendPlayerStatus() {
        if (BuildConfig.DEBUG) LOGGER.debug("sendPlayerStatus()");
        playerService.sendPlayerStatus();
        if (BuildConfig.DEBUG) LOGGER.debug("sendPlayerStatus() done");
    }

    private void exit() {
        if (BuildConfig.DEBUG) LOGGER.debug("exit()");
        if (!playerService.streamPlaying) {
            playerService.stopSelf();
        }
        if (BuildConfig.DEBUG) LOGGER.debug("exit() done");
    }

}
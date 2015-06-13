package com.codingspezis.android.metalonly.player.stream;

import android.content.*;
import android.media.*;
import android.telephony.*;

/**
 * MyPhoneStateListener
 *
 * @version < 22.12.2012
 *          <p/>
 *          listener for muting stream at calls
 */
class MyPhoneStateListener extends PhoneStateListener {

    /**
     *
     */
    private final PlayerService playerService;
    private final AudioManager audioManager;
    private boolean mute;

    public MyPhoneStateListener(PlayerService playerService) {
        super();
        this.playerService = playerService;
        audioManager = (AudioManager) this.playerService.getSystemService(Context.AUDIO_SERVICE);
        mute = false;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        // call ended
        if (state == TelephonyManager.CALL_STATE_IDLE) {
            if (mute) {
                audioManager
                        .setStreamMute(AudioManager.STREAM_MUSIC, false);
                mute = false;
            }
            // call active
        } else if (state == TelephonyManager.CALL_STATE_RINGING
                || state == TelephonyManager.CALL_STATE_OFFHOOK) {
            if (!mute) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                mute = true;
            }
        }
    }
}
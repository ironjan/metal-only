package com.codingspezis.android.metalonly.player.stream;

import android.media.*;

import com.spoledge.aacdecoder.*;

/**
 * A class implementing a PlayerCallback with stubs
 */
class DummyPlayerCallback implements PlayerCallback {
    @Override
    public void playerStarted() {
    }

    @Override
    public void playerPCMFeedBuffer(boolean b, int i, int i2) {
    }

    @Override
    public void playerStopped(int i) {
    }

    @Override
    public void playerException(Throwable throwable) {
    }

    @Override
    public void playerMetadata(String s, String s2) {
    }

    @Override
    public void playerAudioTrackCreated(AudioTrack audioTrack) {
    }
}

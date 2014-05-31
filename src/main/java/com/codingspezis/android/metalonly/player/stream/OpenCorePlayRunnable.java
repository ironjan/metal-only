package com.codingspezis.android.metalonly.player.stream;

import android.media.*;
import android.os.*;
import android.os.Process;
import android.util.*;

import com.codingspezis.android.metalonly.player.stream.exceptions.*;
import com.spoledge.aacdecoder.*;

class OpenCorePlayRunnable implements Runnable {
    private static final String TAG = OpenCorePlayRunnable.class.getSimpleName();

    private OpencorePlayer opencorePlayer;
    private final String url;
    private final int expectedKBitSecRate;

    public OpenCorePlayRunnable(OpencorePlayer opencorePlayer, String url, int expectedKBitSecRate) {
        this.opencorePlayer = opencorePlayer;
        this.url = url;
        this.expectedKBitSecRate = expectedKBitSecRate;
    }

    @Override
    @SuppressWarnings("synthetic-access")
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        opencorePlayer.streamPlayerOpencore.wakeLock.acquire();
        opencorePlayer.streamPlayerOpencore.wifiLock.acquire();
        try {
            int samplerateRestarts = 0;
            WrongSampleRateException lastWrongSampleRateException = null;
            while (opencorePlayer.streamPlayerOpencore.shouldPlay && samplerateRestarts < 10) {
                try {
                    opencorePlayer.play(url, expectedKBitSecRate);
                } catch (WrongSampleRateException wsbe) {
                    // ignore exception 10 times, then fail
                    Log.e(TAG, "playAsync(): " + wsbe.getMessage());
                    Log.e(TAG, "playAsync(): restarting playback #" + (++samplerateRestarts));
                    sleep500ms();
                }
            }
            if(lastWrongSampleRateException != null){
                getPlayerCallback().playerException(lastWrongSampleRateException);
            }
        } catch (Exception e) {
            Log.e(TAG, "playAsync():", e);
            getPlayerCallback().playerException(e);
        }
        opencorePlayer.streamPlayerOpencore.wakeLock.release();
        opencorePlayer.streamPlayerOpencore.wifiLock.release();
    }

    private void sleep500ms() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private PlayerCallback getPlayerCallback() {
        final PlayerCallback opencorePlayerPlayerCallback = opencorePlayer.getPlayerCallback();
        if (opencorePlayerPlayerCallback != null) {
            return opencorePlayerPlayerCallback;
        }

        return sDummyCallback;
    }

    PlayerCallback sDummyCallback = new PlayerCallback() {
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
    };
}

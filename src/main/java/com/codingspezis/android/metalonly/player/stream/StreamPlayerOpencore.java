package com.codingspezis.android.metalonly.player.stream;

import android.content.*;
import android.media.*;
import android.net.wifi.*;
import android.os.*;
import android.util.*;

import com.codingspezis.android.metalonly.player.*;
import com.spoledge.aacdecoder.*;

import java.util.*;

/**
 * stream player that is using opencore port aacdecoder-android
 */
public class StreamPlayerOpencore implements AudioStream {

    private OpencorePlayer ocPlayer;
    private OnStreamListener streamListener;
    boolean shouldPlay = false;
    private String url;
    private Context context;

    // locks
    PowerManager.WakeLock wakeLock;
    WifiManager.WifiLock wifiLock;

    /**
     * constructor
     */
    public StreamPlayerOpencore(Context context) {
        this.context = context;
        createLocks(context);
        createPlayer();
    }

    private void createLocks(Context context) {
        // wake
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "WAKE_LOCK_STREAM_DECODER");
        // wifi
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL,
                "WIFI_LOCK_STREAM_DECODER");
    }

    private void createPlayer() {
        if (ocPlayer != null) {
            ocPlayer.stop();
        }
        ocPlayer = new OpencorePlayer(this, callback);
        ocPlayer.setMetadataEnabled(true);
        ocPlayer.setMetadataCharEnc("ISO-8859-1");
    }

    public void setAudioBufferCapacityMs(int ms) {
        if (ocPlayer != null)
            ocPlayer.setAudioBufferCapacityMs(ms);
    }

    public void setDecodingBufferCapacityMs(int ms) {
        if (ocPlayer != null)
            ocPlayer.setDecodeBufferCapacityMs(ms);
    }

    /**
     * setter for url
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void startPlaying() {
        stopPlaying();
        shouldPlay = true;
        ocPlayer.playAsync(url);
    }

    @Override
    public void stopPlaying() {
        shouldPlay = false;
        ocPlayer.stop();
    }

    @Override
    public void setOnStreamListener(OnStreamListener streamListener) {
        this.streamListener = streamListener;
    }

    @Override
    public boolean isPlaying() {
        return ocPlayer.isPlaying();
    }

    /**
     * this is the callback object for instances of AACPlayerModified
     */
    private final PlayerCallback callback = new PlayerCallback() {

        public static final long CRITICAL_EXCEPTION_INTERVAL_MS = 1000 * 60;
        public static final long CRITICAL_EXCEPTION_NUMBER = 3;

        private long lastExceptionWave = 0;
        private int exceptionCounter = 0;

        @Override
        public void playerStarted() {
            if (shouldPlay) {
                if (streamListener != null) {
                    streamListener.streamConnected();
                }
            } else {
                ocPlayer.stop();
            }
        }

        @Override
        public void playerMetadata(String arg0, String arg1) {
            if (arg0 != null && arg0.equals("StreamTitle")) {
                if (streamListener != null) {
                    streamListener.metadataReceived(arg1);
                }
            }
        }

        @Override
        public void playerPCMFeedBuffer(boolean arg0, int arg1, int arg2) {
        }

        @Override
        public void playerStopped(int arg0) {
            // if(shouldPlay)
            // ocPlayer.playAsync(url);
        }

        @Override
        public void playerException(Throwable arg0) {
            createPlayer();
            long currentTime = Calendar.getInstance().getTimeInMillis();
            // was there an exception within the last minute?
            if ((currentTime - lastExceptionWave) > CRITICAL_EXCEPTION_INTERVAL_MS) {
                exceptionCounter = 0;
                lastExceptionWave = currentTime;
            }
            // were there less than 3 exceptions within the last minute?
            if (exceptionCounter++ < CRITICAL_EXCEPTION_NUMBER) {
                if (shouldPlay) {
                    // only make some toast and restart stream
                    String restartMessage = context.getString(R.string.stream_restart).replace("%s", Integer.toString(exceptionCounter));
                    streamListener.errorOccurred(restartMessage, true);
                    try {
                        Thread.sleep(1000 * exceptionCounter);
                    } catch (InterruptedException e) {
                        Log.e(StreamPlayerOpencore.class.getSimpleName(), e.getMessage(), e);
                    }
                    if (shouldPlay) startPlaying();
                    return;
                }
            }
            if (streamListener != null) {
                streamListener.errorOccurred(
                        "Decoder Fehler: " + arg0.getMessage(), false);
            }
        }

        @Override
        public void playerAudioTrackCreated(AudioTrack arg0) {
        }

    };

}
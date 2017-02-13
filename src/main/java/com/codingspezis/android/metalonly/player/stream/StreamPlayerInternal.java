package com.codingspezis.android.metalonly.player.stream;

import android.content.*;
import android.media.*;
import android.net.wifi.*;
import android.os.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.stream.metadata.*;
import com.codingspezis.android.metalonly.player.utils.UrlConstants;

import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * Created by r on 17.09.14.
 */
public class StreamPlayerInternal implements AudioStream {

    // logger
    private static final String TAG = StreamPlayerInternal.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(TAG);
    private static MediaPlayer mediaPlayer;
    private Context context;
    private OnStreamListener onStreamListener;
    private MetadataListener metadataListener;
    private TimeoutListener timeoutListener;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;
    /**
     * listener for preparation of the media player instance
     */
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if (BuildConfig.DEBUG) LOGGER.debug("onPrepared()");
            acquireLocks();
            if (onStreamListener != null)
                onStreamListener.streamConnected();
            mediaPlayer.start();
            new Thread(metadataListener).start();
            new Thread(timeoutListener).start();
        }
    };
    /**
     * listener for errors of the media player instance
     */
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            // "In this case, the application must release the MediaPlayer object and instantiate a new one."
            // http://developer.android.com/reference/android/media/MediaPlayer.html#MEDIA_ERROR_SERVER_DIED
            if (i == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
                createPlayer();
            if (onStreamListener != null) {
                onStreamListener.errorOccurred("Error " + i + "-" + i2, false);
            }
            metadataListener.stop();
            // TODO: try to restart the stream
            return false;
        }
    };

    /**
     * constructor
     *
     * @param context parents context
     */
    public StreamPlayerInternal(Context context) {
        this.context = context;
        setupMetadataListener();
        setupTimeoutListener();
        createLocks(context);
        createPlayer();
    }

    /**
     * static version of the player running check
     *
     * @return true if decoding and playing is still running
     */
    public static boolean IsPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * creates a wake lock and a wifi lock
     *
     * @param context
     */
    private void createLocks(Context context) {
        // wake
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WAKE_LOCK_STREAM_DECODER");
        // wifi
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "WIFI_LOCK_STREAM_DECODER");
    }

    /**
     * acquires wifi and wake lock
     */
    private void acquireLocks() {
        if (!wakeLock.isHeld())
            wakeLock.acquire(); // TODO: check if mediaPlayer.setWakeMode(..); is better
        if (!wifiLock.isHeld())
            wifiLock.acquire();
    }

    /**
     * releases wifi and wake lock
     */
    private void releaseLocks() {
        if (wakeLock.isHeld())
            wakeLock.release();
        if (wifiLock.isHeld())
            wifiLock.release();
    }

    /**
     * sets up the metadata listener
     */
    private void setupMetadataListener() {
        metadataListener = new MetadataListener();
        metadataListener.setOnMetadataReceivedListener(new OnMetadataReceivedListener() {
            @Override
            public void onMetadataReceived(String metadata) {
                if (onStreamListener != null)
                    onStreamListener.metadataReceived(metadata);
            }

            @Override
            public void onMetadataError(Exception exception) {
                if (onStreamListener != null)
                    onStreamListener.errorOccurred(exception.getMessage(), true);
            }
        });
    }

    /**
     * sets up the timeout listener
     */
    private void setupTimeoutListener() {
        timeoutListener = new TimeoutListener();
        timeoutListener.setOnTimeoutListener(new OnTimeoutListener() {
            private static final long TIMEOUT_LIMIT = 30 * 1000; // 30 sec
            private long lastTimeout;

            @Override
            public void onTimeout() {
                long currentTimeout = Calendar.getInstance().getTimeInMillis();
                // do not allow more than one reconnect within TIMEOUT_LIMIT milliseconds
                if ((currentTimeout - lastTimeout) > TIMEOUT_LIMIT)
                    startPlaying();
                else {
                    stopPlaying();
                    if (onStreamListener != null)
                        onStreamListener.errorOccurred(context.getString(R.string.timeout), false);
                }
                lastTimeout = currentTimeout;
            }
        });
    }

    /**
     * creates a media player instance
     */
    private void createPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
    }

    /**
     * resets the media player instance
     *
     * @throws IOException throws an exception if the data source couldn't be set
     */
    private void resetPlayer() throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(UrlConstants.STREAM_URL_128);
        metadataListener.stop();
        timeoutListener.stop();
    }

    /**
     * starts decoding and playing stream
     */
    public void startPlaying() {
        if (BuildConfig.DEBUG) LOGGER.debug("startPlaying()");
        try {
            resetPlayer();
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            if (onStreamListener != null) {
                onStreamListener.errorOccurred(context.getString(R.string.error_initialize_player), false); // should never happen
            }
        }
    }

    /**
     * stops decoding and playing stream
     */
    public void stopPlaying() {
        if (BuildConfig.DEBUG) LOGGER.debug("stopPlaying()");
        metadataListener.stop();
        timeoutListener.stop();
        mediaPlayer.stop();
        mediaPlayer.reset();
        releaseLocks();
    }

    /**
     * @return true if decoding and playing is still running
     */
    public boolean isPlaying() {
        return IsPlaying();
    }

    /**
     * sets listener for handle metadata etc.
     *
     * @param streamListener listener to set
     */
    public void setOnStreamListener(OnStreamListener streamListener) {
        onStreamListener = streamListener;
    }

}

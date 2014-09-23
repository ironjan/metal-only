package com.codingspezis.android.metalonly.player.stream;

import android.app.*;
import android.content.*;
import android.os.*;
import android.telephony.*;
import android.util.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.favorites.*;
import com.spoledge.aacdecoder.*;

/**
 * service that is managing stream player
 * // TODO refactor this class
 */
public class PlayerService extends Service {

    public static final String INTENT_PLAY = "MO_INTENT_PLAY";
    public static final String INTENT_STOP = "MO_INTENT_STOP";
    public static final String INTENT_STATUS_REQUEST = "MO_INTENT_STATUS_REQUEST";
    public static final String INTENT_STATUS = "MO_INTENT_STATUS";
    public static final String INTENT_METADATA = "MO_INTENT_METADATA";
    public static final String INTENT_EXIT = "MO_INTENT_EXIT";

    public static final int FOREGROUND_NOTIFICATION_ID = 1;
    public static final int TIME_15_MINUTES_IN_MILLIS = 15 * 60 * 1000;
    public static final String DEFAULT_PREF_AUDIO_BUFFER = String.valueOf(AACPlayer.DEFAULT_AUDIO_BUFFER_CAPACITY_MS);
    public static final String DEFAULT_PREF_DECODING_BUFFER = String.valueOf(AACPlayer.DEFAULT_DECODE_BUFFER_CAPACITY_MS);
    private NotificationManager notificationManager;

    public static final String BROADCAST_EXTRA_CONNECTED = "MO_EXTRA_CONNECTED";
    public static final String BROADCAST_EXTRA_META = "MO_EXTRA_META";

    // JSON file name for favorites
    public static final String JSON_FILE_HIST = "mo_hist.json";

    public static final int MAXIMUM_NUMBER_OF_HISTORY_SONGS = 25;

    // public AudioStream audioStream;
    public AudioStream audioStream;

    private PlayerBCReceiver playerBCReceiver;

    private MyPhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    StreamWatcher streamWatcher;
    private SongSaver historySaver;
    final PlayerService playerService = this;

    // this should be the state of the stream player
    boolean streamPlaying = false;

    @Override
    public void onCreate() {
        super.onCreate();

        playerBCReceiver = new PlayerBCReceiver(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        phoneStateListener = new MyPhoneStateListener(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        streamWatcher = new StreamWatcher(this);
        historySaver = new SongSaver(this, JSON_FILE_HIST, MAXIMUM_NUMBER_OF_HISTORY_SONGS);

        registerReceiver(playerBCReceiver, new IntentFilter(INTENT_PLAY));
        registerReceiver(playerBCReceiver, new IntentFilter(INTENT_STOP));
        registerReceiver(playerBCReceiver, new IntentFilter(
                INTENT_STATUS_REQUEST));
        registerReceiver(playerBCReceiver, new IntentFilter(INTENT_EXIT));

        audioStream = new StreamPlayerInternal(this);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        unregisterReceiver(playerBCReceiver);
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    private Notification generateNotification(String contentText) {
        CharSequence tickerText = getString(R.string.playing);
        long when = System.currentTimeMillis();
        CharSequence contentTitle = getString(R.string.app_name);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        Notification not = new Notification(R.drawable.mo_notify, tickerText, when);
        not.setLatestEventInfo(getApplicationContext(),
                contentTitle,
                contentText,
                contentIntent);
        not.flags = not.flags | Notification.FLAG_ONGOING_EVENT;
        return not;
    }

    public void setForeground() {
        startForeground(FOREGROUND_NOTIFICATION_ID, generateNotification(getString(R.string.playing)));
    }

    void notify(String contentText) {
        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, generateNotification(contentText));
    }

    /**
     * clears data so player is in stopped mode
     */
    void clear() {
        streamPlaying = false;
        stopForeground(true);
        streamWatcher.deleteMetadata();
    }

    /**
     * adds a song to history if it wasn't played last 15 minutes
     *
     * @param metadata meta data to parse to song
     */
    void addSongToHistory(String metadata) {
        Song song = (new MetadataParser(metadata)).toSong();
        boolean canAdd = false;
        if (song.isValid()) {
            int index = historySaver.isAlreadyIn(song);
            long timeDiff = song.date - historySaver.get(index).date;
            if (index == -1) {
                canAdd = true;
            } else if (timeDiff > TIME_15_MINUTES_IN_MILLIS) {
                canAdd = true;
            }
        }
        if (canAdd) {
            historySaver.queeIn(song);
            historySaver.saveSongsToStorage();
        }

    }

    /**
     * sends status of player via broadcast
     */
    void sendPlayerStatus() {
        Intent tmpIntent = new Intent(INTENT_STATUS);
        if (streamPlaying) {
            tmpIntent.putExtra(BROADCAST_EXTRA_CONNECTED, true);
            tmpIntent.putExtra(BROADCAST_EXTRA_META, streamWatcher.getMetadata());
        } else {
            tmpIntent.putExtra(BROADCAST_EXTRA_CONNECTED, false);
            tmpIntent.putExtra(BROADCAST_EXTRA_META, "");
        }
        sendBroadcast(tmpIntent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
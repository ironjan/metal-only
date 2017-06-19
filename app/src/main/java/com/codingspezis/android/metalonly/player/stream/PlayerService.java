package com.codingspezis.android.metalonly.player.stream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.StreamControlActivity_;
import com.codingspezis.android.metalonly.player.core.HistoricTrack;
import com.codingspezis.android.metalonly.player.favorites.SongSaver;
import com.codingspezis.android.metalonly.player.stream.metadata.MetadataFactory;

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
    public static final String BROADCAST_EXTRA_CONNECTED = "MO_EXTRA_CONNECTED";
    public static final String BROADCAST_EXTRA_META = "MO_EXTRA_META";
    // JSON file name for favorites
    public static final String JSON_FILE_HIST = "mo_hist.json";
    public static final int MAXIMUM_NUMBER_OF_HISTORY_SONGS = 25;
    final PlayerService playerService = this;
    // public AudioStream audioStream;
    public AudioStream audioStream;
    StreamWatcher streamWatcher;
    // this should be the state of the stream player
    boolean streamPlaying = false;
    private NotificationManager notificationManager;
    private PlayerBCReceiver playerBCReceiver;
    private MyPhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private SongSaver historySaver;

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
        Intent notificationIntent = StreamControlActivity_.intent(this ).get();
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.mo_notify)
                .setTicker(tickerText)
                .setWhen(when)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .build();

        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        return notification;
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
        HistoricTrack song = (MetadataFactory.INSTANCE.createFromString(metadata)).toSong();
        boolean canAdd = false;
        if (song.isValid()) {
            int index = historySaver.isAlreadyIn(song);
            if (index == -1) {
                canAdd = true;
            } else {
                long timeDiff = song.getPlayedAtAsLong() - historySaver.get(index).getPlayedAtAsLong();
                if (timeDiff > TIME_15_MINUTES_IN_MILLIS) {
                    canAdd = true;
                }
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

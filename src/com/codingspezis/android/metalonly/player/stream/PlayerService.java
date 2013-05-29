package com.codingspezis.android.metalonly.player.stream;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.telephony.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.favorites.*;

/**
 * 
 * service that is managing stream player
 * 
 */
public class PlayerService extends Service {

	// broadcast message keys
	public static final String INTENT_PLAY = "MO_INTENT_PLAY";
	public static final String INTENT_STOP = "MO_INTENT_STOP";
	public static final String INTENT_STATUS_REQUEST = "MO_INTENT_STATUS_REQUEST";
	public static final String INTENT_STATUS = "MO_INTENT_STATUS";
	public static final String INTENT_METADATA = "MO_INTENT_METADATA";
	public static final String INTENT_EXIT = "MO_INTENT_EXIT";

	// broadcast extra keys
	public static final String EXTRA_CONNECTED = "MO_EXTRA_CONNECTED";
	public static final String EXTRA_META = "MO_EXTRA_META";

	// shared preferences keys
	public static final String SP_HISTORY = "MO_SP_HISTROY";

	// maximum number of songs in history
	public static final int HISTORY_ENTRIES = 25;

	// stream URLs
	public static final String URL128 = "http://server1.blitz-stream.de:4400";
	public static final String URL32 = "http://mobil.metal-only.de:8000";

	// private AudioStream audioStream;
	StreamPlayerOpencore audioStream;

	private PlayerBCReceiver playerBCReceiver;
	private NotificationManager notificationManager;
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
		historySaver = new SongSaver(this, SP_HISTORY, HISTORY_ENTRIES);

		registerReceiver(playerBCReceiver, new IntentFilter(INTENT_PLAY));
		registerReceiver(playerBCReceiver, new IntentFilter(INTENT_STOP));
		registerReceiver(playerBCReceiver, new IntentFilter(
				INTENT_STATUS_REQUEST));
		registerReceiver(playerBCReceiver, new IntentFilter(INTENT_EXIT));

		audioStream = new StreamPlayerOpencore(this);
	}

	@Override
	public void onDestroy() {
		notificationManager.cancel(1);
		unregisterReceiver(playerBCReceiver);
		super.onDestroy();
	}

	/**
	 * instantiates music stream with selected player class (from
	 * SettingsActivity)
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	void instantiateSelectedPlayer() {
		SharedPreferences prefs = getSharedPreferences(
				getString(R.string.app_name), Context.MODE_MULTI_PROCESS);
		String rate = prefs.getString(getString(R.string.settings_key_rate),
				getResources().getStringArray(R.array.rate_label)[0]);
		if (rate.equals(getResources().getStringArray(R.array.rate_label)[0])) {
			audioStream.setUrl(URL32);
		} else {
			audioStream.setUrl(URL128);
		}
	}

	/**
	 * generates a system notify
	 * 
	 * @param contentText
	 *            text that should be displayed in the notify
	 */
	@SuppressWarnings("deprecation")
	void notify(String contentText) {
		if (audioStream != null && audioStream.isPlaying()) {
			// text and icon
			CharSequence tickerText = getResources()
					.getString(R.string.playing);
			long when = System.currentTimeMillis();
			CharSequence contentTitle = getResources().getString(
					R.string.app_name);
			// intent
			Intent notificationIntent = new Intent(getApplicationContext(),
					MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, notificationIntent, 0);
			Notification notification = new Notification(R.drawable.mo_notify,
					tickerText, when); // alternative Notivication.Builder
			notification.setLatestEventInfo(getApplicationContext(),
					contentTitle, contentText, contentIntent); // is not
																// available in
																// API level 7
			// notification.flags = notification.flags |
			// Notification.FLAG_NO_CLEAR;
			notification.flags = notification.flags
					| Notification.FLAG_ONGOING_EVENT;
			notificationManager.notify(1, notification);
		}
	}

	/**
	 * clears data so player is in stopped mode
	 */
	void clear() {
		streamPlaying = false;
		notificationManager.cancel(1);
		streamWatcher.deleteMetadata();
	}

	/**
	 * adds a song to history if it wasn't played last 15 minutes
	 * 
	 * @param metadata
	 *            meta data to parse to song
	 */
	void addSongToHistory(String metadata) {
		Song song = (new MetadataParser(metadata)).toSong();
		boolean canAdd = false;
		if (song.isValid()) {
			int index = historySaver.isAlreadyIn(song);
			if (index == -1) {
				canAdd = true;
			} else {
				// add the current song to the history only if it was not played
				// last 15 minutes
				long timeDiff = song.date - historySaver.get(index).date;
				if (timeDiff > (15 * 60 * 1000)) {
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
			tmpIntent.putExtra(EXTRA_CONNECTED, true);
			tmpIntent.putExtra(EXTRA_META, streamWatcher.getMetadata());
		} else {
			tmpIntent.putExtra(EXTRA_CONNECTED, false);
			tmpIntent.putExtra(EXTRA_META, "");
		}
		sendBroadcast(tmpIntent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
package com.codingspezis.android.metalonly.player;

import java.util.Calendar;

import com.codingspezis.android.metalonly.player.MainActivity.MetadataParser;
import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.SongSaver.Song;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * PlayerService
 * @version 24.02.2013
 *
 * service that is managing stream player
 *
 */
public class PlayerService extends Service{
	
	// broadcast message keys
	public static final String INTENT_PLAY =			"MO_INTENT_PLAY";
	public static final String INTENT_STOP =		    "MO_INTENT_STOP";
	public static final String INTENT_STATUS_REQUEST =	"MO_INTENT_STATUS_REQUEST";
	public static final String INTENT_STATUS =  		"MO_INTENT_STATUS";
	public static final String INTENT_METADATA =		"MO_INTENT_METADATA";
	public static final String INTENT_EXIT =       	   	"MO_INTENT_EXIT";
	
	// broadcast extra keys
	public static final String EXTRA_CONNECTED =        "MO_EXTRA_CONNECTED";
	public static final String EXTRA_META =           	"MO_EXTRA_META";
	
	// shared preferences keys
	public static final String SP_HISTORY =				"MO_SP_HISTROY";
	
	// maximum number of songs in history
	public static final int HISTORY_ENTRIES =	 		25;
	
	// stream URLs
	public static final String URL128 = 				"http://server1.blitz-stream.de:4400";
	public static final String URL32  = 				"http://mobil.metal-only.de:8000";
	
	// private AudioStream 			audioStream;
	private StreamPlayerOpencore	audioStream;
	
	private PlayerBCReceiver 		playerBCReceiver;
	private NotificationManager 	notificationManager;
	private MyPhoneStateListener 	phoneStateListener;
	private TelephonyManager 		telephonyManager;
	private StreamWatcher			streamWatcher;
	private SongSaver				historySaver;
	private PlayerService 			playerService = this;
	
	// this should be the state of the stream player
	private boolean streamPlaying = false;
	
	@Override
	public void onCreate(){	
		super.onCreate();
		
		playerBCReceiver = new PlayerBCReceiver();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		phoneStateListener = new MyPhoneStateListener();
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		streamWatcher = new StreamWatcher(); 
		historySaver = new SongSaver(this, SP_HISTORY, HISTORY_ENTRIES);
		
		registerReceiver(playerBCReceiver, new IntentFilter(INTENT_PLAY));   
		registerReceiver(playerBCReceiver, new IntentFilter(INTENT_STOP));
		registerReceiver(playerBCReceiver, new IntentFilter(INTENT_STATUS_REQUEST));
		registerReceiver(playerBCReceiver, new IntentFilter(INTENT_EXIT));
		
		audioStream = new StreamPlayerOpencore(this);
	}
	
	@Override
	public void onDestroy(){
		notificationManager.cancel(1);		
		unregisterReceiver(playerBCReceiver); 
		super.onDestroy();
	}
	
	/**
	 * instantiates music stream with selected player class (from SettingsActivity)
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void instantiateSelectedPlayer(){
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_MULTI_PROCESS);
		String rate = prefs.getString(getString(R.string.settings_key_rate), getResources().getStringArray(R.array.rate_label)[0]);
		if(rate.equals(getResources().getStringArray(R.array.rate_label)[0])) // 32 kb/s
			audioStream.setUrl(URL32);
		else // 128 kb/s (rateIndex == 1)
			audioStream.setUrl(URL128);
	}
	
	/**
	 * generates a system notify
	 * @param contentText text that should be displayed in the notify
	 */
	@SuppressWarnings("deprecation")
	private void notify(String contentText){
		if(audioStream!=null && audioStream.isPlaying()){
			// text and icon
	        CharSequence tickerText = getResources().getString(R.string.playing);
	        long when = System.currentTimeMillis();
	        CharSequence contentTitle = getResources().getString(R.string.app_name);
	        // intent
	        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
	        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
	        Notification notification = new Notification(R.drawable.mo_notify, tickerText, when);					// alternative Notivication.Builder
	        notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);		// is not available in API level 7
	        //notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;
	        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
	        notificationManager.notify(1, notification);
		} 
	}
	
	/**
	 * PlayerBCReceiver
	 * @version 25.12.2012
	 *
	 * broadcast receiver that handles messages for this service
	 *
	 */
	public class PlayerBCReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent){
			// start stream
			if(intent.getAction().equals(INTENT_PLAY)){
				if(audioStream!=null)
					audioStream.stopPlaying();
				instantiateSelectedPlayer();
				streamPlaying = true;
				audioStream.setOnStreamListener(streamWatcher);
				audioStream.startPlaying();
			}
			// stop stream
			else if(intent.getAction().equals(INTENT_STOP)){
				if(audioStream!=null)
					audioStream.stopPlaying();
				clear();
			}
			// playing request
			else if(intent.getAction().equals(INTENT_STATUS_REQUEST)){
				sendPlayerStatus();
			}
			// exit
			else if(intent.getAction().equals(INTENT_EXIT)){
				if(!streamPlaying)
					stopSelf();
			}
		}
		
	}
	
	/**
	 * clears data so player is in stopped mode
	 */
	private void clear(){
		streamPlaying=false;
		notificationManager.cancel(1);
		streamWatcher.deleteMetadata();
	}
	
	/**
	 * adds a song to history if it wasn't played last 15 minutes
	 * @param metadata meta data to parse to song
	 */
	private void addSongToHistory(String metadata){
		Song song = (new MetadataParser(metadata)).toSong();
		boolean canAdd = false;
		if(song.isValid()){
			song.date = Calendar.getInstance().getTimeInMillis();
			int index = historySaver.isAlreadyIn(song);
			if(index==-1){
				canAdd = true;
			}else{
				// add the current song to the history only if it was not played last 15 minutes
				long timeDiff = song.date - historySaver.get(index).date;
				if(timeDiff > (15*60*1000)){
					canAdd = true;
				}
			}
		}
		if(canAdd){
			historySaver.queeIn(song);
			historySaver.saveSongsToStorage();
		}
		
	}
	
	/**
	 * sends status of player via broadcast
	 */
	private void sendPlayerStatus(){
		Intent tmpIntent = new Intent(INTENT_STATUS);
		if(streamPlaying){
			tmpIntent.putExtra(EXTRA_CONNECTED, true);
			tmpIntent.putExtra(EXTRA_META, streamWatcher.getMetadata());
		}else{
			tmpIntent.putExtra(EXTRA_CONNECTED, false);
			tmpIntent.putExtra(EXTRA_META, "");
		}
		sendBroadcast(tmpIntent);
	}
	
	/**
	 * StreamWatcher
	 * @version 22.12.2012
	 *
	 * listener for stream (e.g. meta data)
	 *
	 */
	private class StreamWatcher implements OnStreamListener{

		private String metadata;
		
		@Override
		public void metadataReceived(String data) {
			if(!data.equals(metadata) && data.trim().length()!=0){
				// here we have new meta data
				metadata = data;
				addSongToHistory(metadata);
				playerService.notify(metadata);
				
				Intent metaIntent = new Intent(INTENT_METADATA);
				metaIntent.putExtra(EXTRA_META, data);
				sendBroadcast(metaIntent);
			}
		}
		
		public String getMetadata(){
			return metadata;
		}
		
		public void deleteMetadata(){
			metadata = null;
		}

		@Override
		public void errorOccurred(final String err, final boolean canPlay) {
			(new Handler(playerService.getMainLooper())).post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(playerService, err, Toast.LENGTH_LONG).show();
					if(!canPlay){
						Intent tmpIntent = new Intent(INTENT_STATUS);
						tmpIntent.putExtra(EXTRA_CONNECTED, false);
						sendBroadcast(tmpIntent);
					}
				}
			});
		}

		@Override
		public void streamTimeout() {
			clear(); // TODO: clean clean method
			
			/*
			 * TODO: make som noise here 
			 *
			Intent tmpIntent = new Intent(timeoutIntendAction);
			sendBroadcast(tmpIntent);
			*/
		}

		@Override
		public void streamConnected() {
			sendPlayerStatus();
		}
		
	}
	
	/**
	 * MyPhoneStateListener
	 * @version < 22.12.2012
	 *
	 * listener for muting stream at calls
	 *
	 */
	private class MyPhoneStateListener extends PhoneStateListener{
		
		private boolean 		mute;
		private AudioManager 	audioManager;
		
		public MyPhoneStateListener() {
			super();
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			mute=false;
		}
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			// call ended
			if(state == TelephonyManager.CALL_STATE_IDLE){
				if(mute){
					audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
					mute=false;
				}
			// call active
			}else if(state == TelephonyManager.CALL_STATE_RINGING ||
					 state == TelephonyManager.CALL_STATE_OFFHOOK){
				if(!mute){
					audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
					mute=true;
				}
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
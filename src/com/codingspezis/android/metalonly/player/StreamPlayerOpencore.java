package com.codingspezis.android.metalonly.player;

import android.content.Context;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;

import com.spoledge.aacdecoder.Decoder;
import com.spoledge.aacdecoder.MultiPlayer;
import com.spoledge.aacdecoder.PlayerCallback;

/**
 * StreamPlayerAACDec
 * @version 12.04.2013
 *
 * stream player that is using opencore port aacdecoder-android
 *
 */
public class StreamPlayerOpencore implements AudioStream{
	
	private OpencorePlayer ocPlayer;
	private OnStreamListener streamListener;
	private boolean shouldPlay = false;
	private String url;

	private PowerManager.WakeLock wakeLock;
	
	/**
	 * constructor
	 * @param url stream URL
	 */
	public StreamPlayerOpencore(Context context){
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    	wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WAKE_LOCK_STREAM_DECODER");
		createPlayer();
	}
	
	private void createPlayer(){
		if(ocPlayer!=null)
			ocPlayer.stop();
		ocPlayer = new OpencorePlayer(callback); // playerCallback is defined bellow
		ocPlayer.setMetadataEnabled(true); 
	}
	
	/**
	 * setter for url
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
		this.streamListener=streamListener;
	}
	
	@Override
	public boolean isPlaying(){
		return ocPlayer.isPlaying();
	}
	
	/**
	 * this is the callback object for instances of AACPlayerModified
	 */
	private PlayerCallback callback = new PlayerCallback() {
		@Override
		public void playerStarted() {
			if(shouldPlay){
				if(streamListener!=null)
					streamListener.streamConnected();
			}else{
				ocPlayer.stop();
			}
		}
		@Override
		public void playerMetadata(String arg0, String arg1) {
			if(arg0!=null && arg0.equals("StreamTitle")){
				if(streamListener!=null)
					streamListener.metadataReceived(arg1);
			}
		}
		@Override
		public void playerPCMFeedBuffer(boolean arg0, int arg1, int arg2) {}
		@Override
		public void playerStopped(int arg0) {
//			if(shouldPlay)
//				ocPlayer.playAsync(url);
		}
		@Override
		public void playerException(Throwable arg0) {
			createPlayer();
			if(streamListener!=null)
				streamListener.errorOccurred("AAC+ Decoder Error "+arg0.getMessage(), false);
		}
	};
	
	/**
	 * AACPlayerModified
	 * @version 09.01.2013
	 *
	 * adds isPlaying functionality to MultiPlayer
	 * sets priority higher
	 * 
	 */
	private class OpencorePlayer extends MultiPlayer{
		
		/**
		 * same as super(cb)
		 * @see AACPlaye(PlayerCallback cb)
		 * @param cb the callback, can be null
		 */
		public OpencorePlayer(PlayerCallback cb){
			super(cb);
		}
		
		/**
		 * is AACPlayer playing?
		 * @return true if playing loop is still working - false otherwise
		 */
		public boolean isPlaying(){
			return !stopped;
		}
		
		/**
		 * difference to original method:
		 * Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
		 */
		@Override
		public void playAsync(final String url, final int expectedKBitSecRate) {
			new Thread(new Runnable() {
	            @Override
				public void run() {
	            	Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
	            	wakeLock.acquire();
	            	while(shouldPlay){
		                try {
		                    play( url, expectedKBitSecRate );
		                }
		                catch (Exception e) {
		                    // Log.e( LOG, "playAsync():", e);
		                	if(e.getMessage()!=null &&
		                	   e.getMessage().equals(Decoder.WORKAROUND_EXCEPTION)){
		                		// reconnection max number? 
		                		Log.e("AACDecoder", "stream could not be started -> trying again");
		                	}else{
		                		if (playerCallback != null) playerCallback.playerException( e );
		                		break;
		                	}
		                }
	            	}
	            	wakeLock.release();
	            }
	        }).start();
	    }
	}

}
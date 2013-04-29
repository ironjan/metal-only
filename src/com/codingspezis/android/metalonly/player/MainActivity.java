package com.codingspezis.android.metalonly.player;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.lazylistmodification.*;
import com.codingspezis.android.metalonly.player.SongSaver.Song;
import com.codingspezis.android.metalonly.player.WishChecker.AllowedActions;

import java.io.BufferedReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * MainActivity
 * 
 * @version 07.02.2013
 * 
 *          main GUI activity
 * 
 * 
 *          TODO: better lazylist TODO: check static string (e.g. PlanGrabber is
 *          useless) TODO: better song saving
 * 
 */
public class MainActivity extends SherlockActivity implements OnClickListener,
		OnItemClickListener {

	// intent keys
	public static final String showToastMessage = "MO_SHOW_TOAST";
	// intent extra key
	public static final String messageExtra = "MO_MESSAGE_EXTRA";
	// intent return codes
	public static final int INTENT_RETURN_FAV = 0;

	// shared preferences keys
	public static final String KEY_SP_MODTHUMBDATE ="MO_SP_MODTHUMBDATE_";
	
	// GUI objects
	private final MainActivity mainActivity = this;
	private ListView listView;
	private ImageView buttonStream;
	private ImageButton buttonVolume;
	private ImageButton buttonCalendar;
	private ImageButton buttonWish;
	private Marquee marqueeMod;
	private Marquee marqueeGenre;
	private Menu menu;

	// other
	private MainBroadcastReceiver broadcastReceiver;
	private SongSaver favoritesSaver;
	private MetadataParser metadataParser;
	private SongSaver historySaver;

	// other variables
	private boolean shouldPlay = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getSupportActionBar().setHomeButtonEnabled(false);
		setContentView(R.layout.main_history);
		setSupportProgressBarIndeterminateVisibility(false);
		
		setUpBroadcastReceiver();
		setUpPlayerService();
		setUpDataObjects();	
		setUpGUIObjects();	
	}

	@Override
	public void onPause() {
		favoritesSaver.saveSongsToStorage();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Intent tmpIntent = new Intent(PlayerService.INTENT_EXIT);
		sendBroadcast(tmpIntent);
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	/**
	 * sets up data objects
	 */
	private void setUpDataObjects() {
		favoritesSaver = new SongSaver(this, KEY_SP_FAVORITE, -1);
		metadataParser = new MetadataParser("-");
	}

	/**
	 * initializes main broadcast receiver with filters
	 */
	private void setUpBroadcastReceiver() {
		broadcastReceiver = new MainBroadcastReceiver();
		registerReceiver(broadcastReceiver, new IntentFilter(
				PlayerService.INTENT_STATUS));
		registerReceiver(broadcastReceiver, new IntentFilter(
				PlayerService.INTENT_METADATA));
		registerReceiver(broadcastReceiver, new IntentFilter(showToastMessage));
	}

	/**
	 * initializes player service and requests status
	 */
	private void setUpPlayerService() {
		Intent playerStartIntent = new Intent(getApplicationContext(),
				PlayerService.class);
		startService(playerStartIntent);
		Intent statusIntent = new Intent(PlayerService.INTENT_STATUS_REQUEST);
		sendBroadcast(statusIntent);
	}

	/**
	 * initializes GUI objects of main activity
	 */
	private void setUpGUIObjects() {
		buttonStream = (ImageView) findViewById(R.id.buttonPlay);
		buttonVolume = (ImageButton) findViewById(R.id.imageButton5);
		buttonCalendar = (ImageButton) findViewById(R.id.btnCalendar);
		buttonWish = (ImageButton) findViewById(R.id.btnWish);
		marqueeMod = (Marquee) findViewById(R.id.marqueeMod);
		marqueeGenre = (Marquee) findViewById(R.id.marqueeGenree);
		listView = (ListView) findViewById(R.id.listView);

		buttonStream.setOnClickListener(this);

		buttonVolume.setOnClickListener(this);
		buttonCalendar.setOnClickListener(this);
		buttonWish.setOnClickListener(this);
		listView.setOnItemClickListener(this);

		toggleStreamButton(false);
		displaySongs();
		clearMetadata();
	}

	/**
	 * displays history songs on screen
	 */
	private void displaySongs() {
		historySaver = new SongSaver(this, PlayerService.SP_HISTORY,
				PlayerService.HISTORY_ENTRIES);
		listView.removeAllViewsInLayout();
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		for (int i = historySaver.size() - 1; i >= 0; i--) {
			HashMap<String, String> dataEntry = new HashMap<String, String>(2);
			dataEntry.put(SongAdapter.KEY_TITLE, historySaver.get(i).title);
			dataEntry.put(SongAdapter.KEY_INTERPRET,
					historySaver.get(i).interpret);
			dataEntry.put(SongAdapter.KEY_THUMB, historySaver.get(i).thumb);
			dataEntry.put(SongAdapter.KEY_DATE,
					longToDateString(historySaver.get(i).date));
			data.add(dataEntry);
		}
		SongAdapter adapter = new SongAdapter(this, data);
		listView.setAdapter(adapter);
	}

	/**
	 * toggles background color of stream button
	 * 
	 * @param listening
	 *            if this value is true the button shows stop from now on;
	 *            otherwise play is shown
	 */
	private void toggleStreamButton(boolean listening) {
		if (listening) {
			buttonStream.setImageResource(R.drawable.mo_stop5);
		} else {
			buttonStream.setImageResource(R.drawable.mo_play5);
		}
	}

	/**
	 * generates options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		// favorites button
		MenuItem fav = menu.add(0, R.id.favorites, 0, R.string.menu_favorites);
		fav.setIcon(R.drawable.mo_star_b5);
		fav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		// menu button
		SubMenu sub = menu.addSubMenu(0, R.id.menu_sub, 0, R.string.menu);
		sub.setIcon(R.drawable.ic_core_unstyled_action_overflow);
		sub.add(0, R.id.settings, 0, R.string.menu_settings);
		sub.add(0, R.id.donation, 0, R.string.menu_donation);
		sub.add(0, R.id.info, 0, R.string.menu_info);
		sub.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	/**
	 * handles menu button actions
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.settings) {
			Intent settingsIntent = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivity(settingsIntent);
		} else if (item.getItemId() == R.id.favorites) {
			favoritesSaver.saveSongsToStorage(); // this shouldn't be needed
													// because of onPause
													// function
			Intent favoritesIntent = new Intent(getApplicationContext(),
					FavoritesActivity.class);
			startActivityForResult(favoritesIntent, INTENT_RETURN_FAV);
		} else if (item.getItemId() == R.id.donation) {
			Intent paypalIntent = new Intent(getApplicationContext(),
					PayPalDonationActivity.class);
			startActivity(paypalIntent);
		} else if (item.getItemId() == R.id.info) {
			Intent aboutIntent = new Intent(getApplicationContext(),
					AboutActivity.class);
			startActivity(aboutIntent);
		} else {
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == INTENT_RETURN_FAV) {
			favoritesSaver.reload();
			displayMetadata();
			super.onResume();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (event.getAction() == KeyEvent.ACTION_UP && menu != null
					&& menu.findItem(R.id.menu_sub) != null) {
				menu.performIdentifierAction(R.id.menu_sub, 0);
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	/** handles button clicks **/
	@Override
	public void onClick(View arg0) {
		// stream start / stop
		if (arg0 == buttonStream) {
			if (shouldPlay) {
				stopListening();
			} else {
				if (!HTTPGrabber.displayNetworkSettingsIfNeeded(this)) {
					startListening();
				}
			}
		}
		// volume
		else if (arg0 == buttonVolume) {
			showVolumeBar();
		}
		// plan
		else if (arg0 == buttonCalendar) {
			if (!HTTPGrabber.displayNetworkSettingsIfNeeded(this)) {
				startPlanActivity();
			}
		}
		// wish
		else if (arg0 == buttonWish) {
			if (!HTTPGrabber.displayNetworkSettingsIfNeeded(this)) {
				startWishActivity();
			}
		}
	}

	private void startPlanActivity() {
		PlanGrabber pg = new PlanGrabber(this,
				"http://www.metal-only.de/botcon/mob.php?action=plan");
		pg.start();
	}

	private void startWishActivity() {
		WishChecker wishChecker = new WishChecker(this, WishActivity.URL_WISHES);
		wishChecker.setOnWishesCheckedListener(new OnWishesCheckedListener() {
			@Override
			public void onWishesChecked(AllowedActions allowedActions) {
				if (allowedActions.moderated) {
					if (allowedActions.wishes || allowedActions.regards) {
						Bundle bundle = new Bundle();
						bundle.putBoolean(WishActivity.KEY_WISHES_ALLOWED,
								allowedActions.wishes);
						bundle.putBoolean(WishActivity.KEY_REGARDS_ALLOWED,
								allowedActions.regards);
						bundle.putString(WishActivity.KEY_NUMBER_OF_WISHES,
								allowedActions.limit);
						Intent wishIntent = new Intent(mainActivity,
								WishActivity.class);
						wishIntent.putExtras(bundle);
						mainActivity.startActivity(wishIntent);
					} else {
						alertMessage(mainActivity, mainActivity
								.getString(R.string.no_wishes_and_regards));
					}
				} else {
					alertMessage(mainActivity,
							mainActivity.getString(R.string.no_moderator));
				}
			}
		});
		wishChecker.start();
	}

	/**
	 * generates an alert dialog and shows it to choose volume level
	 */
	private void showVolumeBar() {
		final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getString(R.string.volume));
		final SeekBar input = new SeekBar(this);
		input.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		input.setProgress(audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC));
		input.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
		});
		alert.setView(input);
		alert.setPositiveButton(getString(R.string.ok), null);
		alert.show();
	}

	/**
	 * sets listening to true sends start intent to PlayerService shows
	 * connecting dialog
	 */
	private void startListening() {
		setSupportProgressBarIndeterminateVisibility(true);
		shouldPlay = true;
		toggleStreamButton(true);
		Intent tmpIntent = new Intent(PlayerService.INTENT_PLAY);
		sendBroadcast(tmpIntent);
	}

	/**
	 * sets listening to false sends stop intent to PlayerService
	 */
	private void stopListening() {
		setSupportProgressBarIndeterminateVisibility(false);
		shouldPlay = false;
		clearMetadata();
		toggleStreamButton(false);
		Intent tmpIntent = new Intent(PlayerService.INTENT_STOP);
		sendBroadcast(tmpIntent);
	}

	/**
	 * displays meta data
	 * 
	 * @param metadata
	 *            data to display
	 */
	private void displayMetadata() {
		if (metadataParser.toSong().isValid() && shouldPlay) {
			marqueeGenre.setText(metadataParser.getGENRE());
			marqueeMod.setText(metadataParser.getMODERATOR());
		}
	}

	/**
	 * clears the metadata display
	 */
	private void clearMetadata() {
		marqueeGenre.setText("");
		marqueeMod.setText("");
		metadataParser = new MetadataParser("-");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final int index = arg2;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.history_options_array,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleAction(historySaver.size() - index - 1, which);
					}
				});
		builder.show();
	}

	/**
	 * handles an action on an index
	 * 
	 * @param index
	 *            item to handle
	 * @param action
	 *            action to handle
	 */
	private void handleAction(final int index, int action) {
		switch (action) {
		case 0: // add to favorites
			Song song = historySaver.get(index);
			if (favoritesSaver.isAlreadyIn(song) == -1) {
				song.thumb = "";
				favoritesSaver.addSong(song);
				Toast.makeText(this, R.string.fav_added, Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(this, R.string.fav_already_in, Toast.LENGTH_LONG)
						.show();
			}
			break;
		case 1: // YouTube
			String searchStr = historySaver.get(index).interpret + " - "
					+ historySaver.get(index).title;
			try {
				searchStr = URLEncoder.encode(searchStr, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Uri url = Uri.parse("http://www.youtube.com/results?search_query="
					+ searchStr);
			Intent youtube = new Intent(Intent.ACTION_VIEW, url);
			startActivity(youtube);
			break;
		case 2: // share
			String message = historySaver.get(index).interpret + " - "
					+ historySaver.get(index).title;
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT, message);
			startActivity(Intent.createChooser(share, getResources()
					.getStringArray(R.array.favorite_options_array)[2]));
			break;
		}
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * static methods * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * *
	 */

	/**
	 * 
	 * @param context
	 * @param msg
	 */
	public static void toastMessage(final Context context, final String msg) {
		(new Handler(context.getMainLooper())).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * 
	 * @param context
	 * @param msg
	 */
	public static void alertMessage(final Context context, final String msg) {
		(new Handler(context.getMainLooper())).post(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setMessage(msg);
				alert.setPositiveButton(context.getString(R.string.ok), null);
				alert.show();
			}
		});
	}

	/**
	 * converts timeMillis to a printable string
	 * 
	 * @param timeMillis
	 *            date as long
	 * @return date as string
	 */
	@SuppressLint("SimpleDateFormat")
	public static String longToDateString(long timeMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-dd.MM.yy");
		return sdf.format(calendar.getTime());
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * classes * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * *
	 */

	/**
	 * MetadataParser
	 * 
	 * @version 27.12.2012
	 * 
	 *          parses interpret title genre and moderator from meta data
	 * 
	 */
	public static class MetadataParser {

		private String INTERPRET;
		private String TITLE;
		private String GENRE;
		private String MODERATOR;

		public MetadataParser(String data) {
			try {
				if (numberOfStars(data) >= 3) {
					String[] slices = data.split("\\*");
					MODERATOR = slices[1].trim();
					GENRE = slices[2].trim();
					data = slices[0].trim();
				} else {
					MODERATOR = "MetalHead OnAir";
					GENRE = "Mixed Metal";
				}
			} catch (Exception e) {
				MODERATOR = "";
				GENRE = "";
			}
			try {
				INTERPRET = data.substring(0, data.indexOf(" - ")).trim();
				TITLE = data.substring(data.indexOf(" - ") + 2).trim();
			} catch (Exception e) {
				INTERPRET = "";
				TITLE = "";
			}
		}

		public String getINTERPRET() {
			return INTERPRET;
		}

		public String getTITLE() {
			return TITLE;
		}

		public String getGENRE() {
			return GENRE;
		}

		public String getMODERATOR() {
			return MODERATOR;
		}

		public Song toSong() {
			Song song = new Song();
			song.interpret = INTERPRET;
			song.title = TITLE;
			song.thumb = MODERATOR;
			if (song.thumb.indexOf(" OnAir") > 0) {
				song.thumb = song.thumb.substring(0,
						song.thumb.indexOf(" OnAir")).trim();
			}
			song.date = Calendar.getInstance().getTimeInMillis();
			return song;
		}

		/**
		 * checks string str for occurrence of '*'
		 * 
		 * @param str
		 *            string to check
		 * @return number of char '*' containing in str
		 */
		private int numberOfStars(String str) {
			if (str.length() == 0) {
				return 0;
			}
			if (str.charAt(0) == '*') {
				return 1 + numberOfStars(str.substring(1));
			} else {
				return numberOfStars(str.substring(1));
			}
		}

	}

	/**
	 * MainBroadcastReceiver
	 * 
	 * @version 25.12.2012
	 * 
	 *          broadcast receiver class for communication between other
	 *          activities or services
	 * 
	 */
	private class MainBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// is playing?
			if (intent.getAction().equals(PlayerService.INTENT_STATUS)) {
				setSupportProgressBarIndeterminateVisibility(false);
				if (intent
						.getBooleanExtra(PlayerService.EXTRA_CONNECTED, false)) {
					shouldPlay = true;
					toggleStreamButton(true);
					metadataParser = new MetadataParser(
							intent.getStringExtra(PlayerService.EXTRA_META));
					displayMetadata();
				} else {
					stopListening();
					toggleStreamButton(false);
				}
				// meta data
			} else if (intent.getAction().equals(PlayerService.INTENT_METADATA)) {
				String metadata = intent
						.getStringExtra(PlayerService.EXTRA_META);
				metadataParser = new MetadataParser(metadata);
				displayMetadata();
				displaySongs();
			}
		}
	}

	/**
	 * PlanGrabber
	 * 
	 * @version 24.02.2013
	 */
	private class PlanGrabber {

		private final Context context;

		private final HTTPGrabber grabber;
		private final OnHTTPGrabberListener listener = new OnHTTPGrabberListener() {

			@Override
			public void onSuccess(BufferedReader httpResponse) {
				// start activity
				try {
					String site = "", line;
					while ((line = httpResponse.readLine()) != null) {
						site += line;
					}
					Bundle bundle = new Bundle();
					bundle.putString(PlanActivity.KEY_SITE, site);
					Intent planIntent = new Intent(getApplicationContext(),
							PlanActivity.class);
					planIntent.putExtras(bundle);
					startActivity(planIntent);
				} catch (Exception e) {
					toastMessage(context, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onTimeout() {
			}

			@Override
			public void onError(String error) {
			}

			@Override
			public void onCancel() {
			}

		};

		/** Constructor */
		public PlanGrabber(Context context, String URL) {
			this.context = context;
			grabber = new HTTPGrabber(context, URL, listener);
		}
		
		public void start(){
			grabber.start();
		}

	}

}


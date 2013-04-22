package com.codingspezis.android.metalonly.player;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.WishChecker.AllowedActions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * WishActivity
 * @version 06.01.2013
 * 
 * activity that is showing a GUI for entering and sending wishes and/or regards to metal-only.de
 * 
 */
public class WishActivity extends SubActivity implements OnClickListener{
	
	// URL to wish script on metal-only.de
	public static final String URL_WISHES = "http://metalonly.de/?action=wunschscript";
	
	// intent keys
	public static final String KEY_WISHES_ALLOWED   = "MO_WISHES_ALLOWED";
	public static final String KEY_REGARDS_ALLOWED  = "MO_REGARDS_ALLOWED";
	public static final String KEY_NUMBER_OF_WISHES = "MO_NUMBER_OF_WISHES";
	public static final String KEY_DEFAULT_INTERPRET= "MO_DEFAULT_INTERPRET";
	public static final String KEY_DEFAULT_TITLE	= "MO_DEFAULT_TITLE";
	
	// shared preferences keys
	public static final String KEY_SP_NICK = "moa_nickname";
	
	boolean wish,regard;
	
	private SharedPreferences settings;
	 
	// buttons
	private Button buttonSend;
	private Button buttonHelp;
	
	// user input
	private EditText editNick;
	private EditText editArtist;
	private EditText editTitle;
	private EditText editRegard;
	
	private String numberOfWishes;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wishes);
		settings = getSharedPreferences(getString(R.string.app_name), Context.MODE_MULTI_PROCESS);
		
		// input fields
		editNick = (EditText) findViewById(R.id.nick);
		editNick.setText(settings.getString(KEY_SP_NICK, ""));
		editArtist = (EditText) findViewById(R.id.artist);
		editTitle = (EditText) findViewById(R.id.title);
		editRegard = (EditText) findViewById(R.id.regard);
		
		buttonHelp = (Button)findViewById(R.id.help);
		buttonSend = (Button)findViewById(R.id.send);
		buttonHelp.setOnClickListener(this);
		buttonSend.setOnClickListener(this);

		// get parameters
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			wish = bundle.getBoolean(KEY_WISHES_ALLOWED, false);
			regard = bundle.getBoolean(KEY_REGARDS_ALLOWED, false);
			numberOfWishes = bundle.getString(KEY_NUMBER_OF_WISHES);
			editArtist.setText(bundle.getString(KEY_DEFAULT_INTERPRET));
			editTitle.setText(bundle.getString(KEY_DEFAULT_TITLE));
		}
		
		((TextView)findViewById(R.id.wishcount)).setText(numberOfWishes);
		if(!wish){
			editArtist.setText(R.string.no_wishes_short);
			editArtist.setEnabled(false);
			editTitle.setText(R.string.no_wishes_short);
			editTitle.setEnabled(false);
		}
		if(!regard){
			editRegard.setText(R.string.no_regards);
			editRegard.setEnabled(false);
		}		
	}
	
	@Override
	public void onDestroy(){
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(KEY_SP_NICK, editNick.getText().toString());
		editor.commit();
		super.onDestroy();
	}
	
	/**
	 * checks if it is possible to wish something
	 * @param allowedActions class for representing what is allowed
	 * @return true if you can wish - false otherwise
	 */
	public static boolean canWishOrDisplayNot(Activity activity, AllowedActions allowedActions){
		if(!allowedActions.moderated){
			MainActivity.alertMessage(activity, activity.getString(R.string.no_moderator));
			return false;
		}else if(!allowedActions.wishes){
			MainActivity.alertMessage(activity, activity.getString(R.string.no_wishes));
			return false;
		}
		return true;
	}
	
	/**
	 * checks edit text objects for valid data
	 * @return true if input is valid - false otherwise
	 */
	private boolean haveValidData(){
		boolean haveNick = editNick.getText().length()!=0;
		boolean haveWish = editArtist.getText().length()!=0 && editTitle.getText().length()!=0 &&
						   editArtist.isEnabled() && editTitle.isEnabled();
		boolean haveRegard = editRegard.getText().length()!=0 && editRegard.isEnabled();
		return haveNick && (haveWish || haveRegard);
	}
	
	@Override
	public void onClick(View v) {
		// send
		if(v == buttonSend){
			if(!haveValidData()){
				runOnUiThread(new Runnable() {
				    @Override
					public void run() {
				    	Toast.makeText(getApplicationContext(),
								   	   R.string.invalid_input,
								   	   Toast.LENGTH_SHORT
				    	).show();
				    }
				});
			}else{
				(new GetSender()).start();
			}
		}else if(v == buttonHelp){
			showInfo();
		}
	}
	
	/**
	 * shows info
	 */
	private void showInfo(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);			
		alert.setTitle(getString(R.string.notification));	
		WebView w = new WebView(this);
		w.loadData(getString(R.string.wish_explanation), "text/html", "utf-8");
		alert.setView(w);
		alert.setPositiveButton(getString(R.string.ok), null);
		alert.show();
	}
	
	/**
	 * GetSender
	 */
	private class GetSender extends Thread{
		@Override
		public void run(){
			// generate  url
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://www.metal-only.de/?action=wunschscript&do=save");
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			// add post values
			if(!editNick.getText().toString().equals(""))
				pairs.add(new BasicNameValuePair("nick",    editNick.getText().toString()));
			if(!editArtist.getText().toString().equals("") &&
				editArtist.isEnabled())
				pairs.add(new BasicNameValuePair("artist",  editArtist.getText().toString()));
			if(!editTitle.getText().toString().equals("") &&
				editTitle.isEnabled())
				pairs.add(new BasicNameValuePair("song",    editTitle.getText().toString()));
			if(!editRegard.getText().toString().equals(""))
				pairs.add(new BasicNameValuePair("greet",   editRegard.getText().toString()));
			
			try {
				// generate entity
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
				entity.setContentEncoding(HTTP.UTF_8);
				// send post
				post.setEntity(entity);
				HttpResponse response = client.execute(post);
				if(!response.getStatusLine().toString().equals("HTTP/1.1 200 OK")){
					Exception e = new Exception(getString(R.string.sending_error));
					throw e;
				}
				// success message
				runOnUiThread(new Runnable() {
				    @Override
					public void run() {
				    	Toast.makeText(getApplicationContext(),
								   	   R.string.sent,
								   	   Toast.LENGTH_SHORT
				    	).show();
				    	finish();
				    }
				});
			} catch (final Exception e) {
				// display http error message
				runOnUiThread(new Runnable() {
				    @Override
					public void run() {
				    	Toast.makeText(getApplicationContext(),
								   e.toString(),
								   Toast.LENGTH_LONG
				    	).show();
				    }
				});	
			}
		}
	}
}

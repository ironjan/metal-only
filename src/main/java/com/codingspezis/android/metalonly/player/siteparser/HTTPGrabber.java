package com.codingspezis.android.metalonly.player.siteparser;

import java.io.*;
import java.net.*;
import java.util.*;

import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.net.*;
import android.os.*;

import com.codingspezis.android.metalonly.player.*;

/**
 * 
 * this class is for sending HTTP GET & receiving response<br/>
 * while communicating it displays a progress dialog<br/>
 * use {@link OnHTTPGrabberListener} to handle events
 * 
 * @author codingspezis.com
 * 
 */
public class HTTPGrabber extends Thread {

	protected Context context;
	protected String URL;
	protected OnHTTPGrabberListener listener;
	protected Handler handler;

	private ProgressDialog progressDialog;
	private boolean canceled;
	private boolean timedout;
	private final long timeoutDelay = 5000;

	/**
	 * constructor (does not communicate)
	 * 
	 * @param context
	 *            GET context
	 * @param URL
	 *            GET URL
	 * @param listener
	 *            this listener allows to handle GET response
	 */
	public HTTPGrabber(Context context, String URL,
			OnHTTPGrabberListener listener) {
		this.context = context;
		this.URL = URL;
		this.listener = listener;
		handler = new Handler();
	}

	@Override
	public void run() {
		setProgressDialogVisible(true);
		// timeout timer setup
		timedout = false;
		Timer timeoutTimer = new Timer(true);
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (!canceled) {
					timedout = true;
					setProgressDialogVisible(false);
					if (listener != null) {
						listener.onTimeout();
					}
				}
			}
		};
		timeoutTimer.schedule(timerTask, timeoutDelay);
		// HTTP part
		URLConnection con;
		try {
			con = (new URL(URL)).openConnection();
			con.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			if (!canceled && !timedout) {
				timeoutTimer.cancel();
				setProgressDialogVisible(false);
				if (listener != null) {
					listener.onSuccess(reader);
				}
			}
			reader.close();
		} catch (Exception e) {
			canceled = true;
			setProgressDialogVisible(false);
			e.printStackTrace();
			MainActivity.toastMessage(context, e.getMessage());
			if (listener != null) {
				listener.onError(e.getMessage());
			}
		}
	}

	/**
	 * this function shows or hides progress dialog
	 * 
	 * @param visible
	 *            visible or not
	 */
	protected void setProgressDialogVisible(final boolean visible) {
		Runnable progressRun = new Runnable() {
			@Override
			public void run() {
				if (visible) {
					progressDialog = ProgressDialog.show(context, "",
							context.getString(R.string.communicating), true,
							true, new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									canceled = true;
									if (listener != null) {
										listener.onCancel();
									}
								}
							});
				} else {
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				}
			}
		};
		handler.post(progressRun);
	}

	/**
	 * displays timeout message
	 */
	protected void displayTimeoutMSG() {
		MainActivity.toastMessage(context, context.getString(R.string.timeout));
	}

	/**
	 * if there is no network connection available this method shows network
	 * settings
	 * 
	 * @param context
	 *            GET context
	 * @return true if network settings will be displayed - false otherwise
	 */
	public static boolean displayNetworkSettingsIfNeeded(final Context context) {
		if (isOnline(context)) {
			return false;
		} else {
			(new Handler(context.getMainLooper())).post(new Runnable() {
				@Override
				public void run() {
					AlertDialog.Builder alert = new AlertDialog.Builder(context);
					alert.setTitle(R.string.no_internet);
					alert.setMessage(R.string.open_internet_settings);
					alert.setNegativeButton(R.string.no, null);
					alert.setPositiveButton(R.string.yes,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent tmpIntent = new Intent(
											Intent.ACTION_MAIN);
									tmpIntent
											.setAction("android.settings.WIRELESS_SETTINGS");
									context.startActivity(tmpIntent);
								}
							});
					alert.show();
				}
			});
			return true;
		}
	}

	/**
	 * checks network connection
	 * 
	 * @return true if there is a network connection false otherwise
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() == null) {
			return false;
		} else {
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		}
	}

}

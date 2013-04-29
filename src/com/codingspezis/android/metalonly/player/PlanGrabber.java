package com.codingspezis.android.metalonly.player;

import java.io.*;


import android.content.*;
import android.os.*;

import com.codingspezis.android.metalonly.player.siteparser.*;
import com.codingspezis.android.metalonly.player.utils.*;

/**
 * PlanGrabber
 * 
 * @version 24.02.2013
 */
class PlanGrabber {

	/**
	 * 
	 */
	private final MainActivity mainActivity;

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
				Intent planIntent = new Intent(PlanGrabber.this.mainActivity.getApplicationContext(),
						PlanActivity.class);
				planIntent.putExtras(bundle);
				PlanGrabber.this.mainActivity.startActivity(planIntent);
			} catch (Exception e) {
				MainActivity.toastMessage(context, e.getMessage());
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
	public PlanGrabber(MainActivity mainActivity, Context context, String URL) {
		this.mainActivity = mainActivity;
		this.context = context;
		grabber = new HTTPGrabber(context, URL, listener);
	}

	public void start() {
		grabber.start();
	}

}
package com.codingspezis.android.metalonly.player;

import java.io.*;

import android.annotation.*;
import android.os.*;
import android.view.*;
import android.widget.*;

/**
 * LicenseActivity
 * 
 * @version 08.01.2013
 * 
 *          displays one of: mit | lgpl | apache
 * 
 */
public class LicenseActivity extends SubActivity {

	/**
	 * OnLicenseReadListener
	 * 
	 * @version 08.01.2013
	 * 
	 *          listener for ThreadedLicenseReader
	 * 
	 */
	public static interface OnLicenseReadListener {
		public void onLicenseRead();
	}

	// bundle keys
	public static final String KEY_BU_LICENSE_NAME = "MO_LICENSE_NAME";
	public static final String KEY_BU_LICENSE_MIT = "mit";
	public static final String KEY_BU_LICENSE_LGPL = "lgpl";
	public static final String KEY_BU_LICENSE_APACHE = "apache";

	@SuppressLint("DefaultLocale")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String license = "";
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			license = bundle.getString(KEY_BU_LICENSE_NAME);
		}
		setContentView(R.layout.license);
		setTitle(license.toUpperCase());
		final TextView textView = (TextView) findViewById(R.id.license);
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
		final ThreadedLicenseReader reader = new ThreadedLicenseReader(license);
		reader.setOnLicenseReadListener(new OnLicenseReadListener() {
			@Override
			public void onLicenseRead() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String license = reader.getLicense();
						if (license != null) {
							textView.setText(license);
						}
						progressBar.setVisibility(View.INVISIBLE);
						textView.setVisibility(View.VISIBLE);
					}
				});
			}
		});
		reader.start();
	}

	/**
	 * ThreadedLicenseReader
	 * 
	 * @version 08.01.2013
	 * 
	 *          loads licenses asynchronous
	 * 
	 */
	private class ThreadedLicenseReader extends Thread {

		// listener
		private OnLicenseReadListener onLicenseReadListener;

		// license
		private String license;

		/**
		 * constructor
		 * 
		 * @param license
		 *            license to read
		 */
		public ThreadedLicenseReader(String license) {
			this.license = license;
		}

		@Override
		public void run() {
			try {
				license = getLicenseText(license);
			} catch (Exception e) {
				license = null;
			}
			if (onLicenseReadListener != null) {
				onLicenseReadListener.onLicenseRead();
			}
		}

		/**
		 * getter for license
		 * 
		 * @return license as string
		 */
		public String getLicense() {
			return license;
		}

		/**
		 * setter for listener
		 * 
		 * @param onLicenseReadListener
		 *            listener
		 */
		public void setOnLicenseReadListener(
				OnLicenseReadListener onLicenseReadListener) {
			this.onLicenseReadListener = onLicenseReadListener;
		}

		/**
		 * returns content of licens files
		 * 
		 * @param license
		 *            one of: mit | lgpl | apache
		 * @return license as string
		 * @throws IOException
		 */
		private String getLicenseText(String license) throws IOException {
			InputStream in;

			if (KEY_BU_LICENSE_APACHE.equals(license)) {
				in = getResources().openRawResource(R.raw.apache);
			} else if (KEY_BU_LICENSE_LGPL.equals(license)) {
				in = getResources().openRawResource(R.raw.lgpl);
			} else if (KEY_BU_LICENSE_MIT.equals(license)) {
				in = getResources().openRawResource(R.raw.apache);
			} else {
				throw new IllegalArgumentException(
						"Argument has to be one of: \"apache\",\"lgpl\",\"mit\"");
			}

			Reader fr = new InputStreamReader(in, "utf-8");
			String s = "";
			int read = -1;
			final int BUFF_SIZE = 256;
			char buffer[] = new char[BUFF_SIZE];
			do {
				read = fr.read(buffer, 0, BUFF_SIZE);
				if(read > 0) s += String.valueOf(buffer, 0, read);
			} while (read == BUFF_SIZE);
			fr.close();
			return s;
		}
	}

}

package com.codingspezis.android.metalonly.player;

import android.annotation.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.licensing.*;

/**
 * 
 * displays one of: mit | lgpl | apache
 * 
 */
public class LicenseActivity extends SubActivity {

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
		final ThreadedLicenseReader reader = new ThreadedLicenseReader(this,
				license);
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

}

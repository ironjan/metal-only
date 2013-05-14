package com.codingspezis.android.metalonly.player;

import android.os.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;

public class SubActivity extends SherlockActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// back button
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else {
			return false;
		}
		return true;
	}

}

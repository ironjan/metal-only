package com.codingspezis.android.metalonly.player;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;

public class SubActivity extends SherlockActivity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// back button
		if(item.getItemId() == android.R.id.home){
			finish();
		}
		else return false;
		return true;
	}
	
}

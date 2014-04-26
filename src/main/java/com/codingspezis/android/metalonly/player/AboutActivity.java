package com.codingspezis.android.metalonly.player;

import android.annotation.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;

import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.*;

/**
 * 
 * this activity shows information about the application e.g. codingspezis.com
 * and used software
 * 
 * @author codingspezis.com
 * 
 */
@EActivity(R.layout.activity_about)
@SuppressLint("Registered")
public class AboutActivity extends SherlockFragmentActivity {



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return false;
    }
}

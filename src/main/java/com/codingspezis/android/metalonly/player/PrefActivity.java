package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;


/**
 * FIXME Replace Sherlock component
 */
@SuppressLint("Registered")
public class PrefActivity extends AppCompatPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO is setTheme necessary?
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // back button
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else return false;
        return true;
    }


}

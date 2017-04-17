package com.codingspezis.android.metalonly.player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.codingspezis.android.metalonly.player.fragments.WishFragment;
import com.codingspezis.android.metalonly.player.wish.AllowedActions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * activity that is showing a GUI for entering and sending wishes and/or regards
 * to metal-only.de
 * TODO use androidannotations
 */

public class WishActivity extends AppCompatActivity {

    public static final String KEY_DEFAULT_INTERPRET = "MO_DEFAULT_INTERPRET";
    public static final String KEY_DEFAULT_TITLE = "MO_DEFAULT_TITLE";

    private static final Logger LOGGER = LoggerFactory.getLogger(WishActivity.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) LOGGER.debug("onCreate({})", savedInstanceState);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wish);


        showContentFragment();

        if (BuildConfig.DEBUG) LOGGER.debug("onCreate({}) done", savedInstanceState);
    }

    private void showContentFragment() {
        if (BuildConfig.DEBUG) LOGGER.debug("showContentFragment()");

        Bundle bundle = getIntent().getExtras();
        WishFragment fragment = WishFragment.newInstance(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, fragment);
        ft.commit();

        if (BuildConfig.DEBUG) LOGGER.debug("showContentFragment() done");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (BuildConfig.DEBUG) LOGGER.debug("");
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, StreamControlActivity_.class);
                NavUtils.navigateUpTo(this, intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
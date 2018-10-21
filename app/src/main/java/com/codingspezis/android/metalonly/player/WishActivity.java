package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;

import com.codingspezis.android.metalonly.player.fragments.WishFragment;
import com.codingspezis.android.metalonly.player.fragments.WishFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;

/**
 * activity that is showing a GUI for entering and sending wishes and/or regards
 * to metal-only.de
 */
@EActivity(R.layout.activity_wish)
@SuppressLint("Registered")
public class WishActivity extends AppCompatActivity {

    public static final String KEY_DEFAULT_INTERPRET = "MO_DEFAULT_INTERPRET";
    public static final String KEY_DEFAULT_TITLE = "MO_DEFAULT_TITLE";

    @Extra(KEY_DEFAULT_INTERPRET)
    String interpret;
    @Extra(KEY_DEFAULT_TITLE)
    String title;

    @AfterViews
    void bindIntentToFragment() {
        Intent intent = getIntent();
        Bundle extras = (intent != null) ? intent.getExtras() : null;
        Bundle fragmentArgs = (extras != null) ? extras : new Bundle();

        WishFragment wishFragment = WishFragment_.Companion.newInstance(fragmentArgs);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, wishFragment)
                .commitAllowingStateLoss();
    }

    @OptionsItem(android.R.id.home)
    void homeClicked(){
        Intent intent = new Intent(this, StreamControlActivity_.class);
        NavUtils.navigateUpTo(this, intent);
    }

}
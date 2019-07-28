package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.codingspezis.android.metalonly.player.utils.UrlConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

/**
 * with this activity users can make paypal donations to metal only
 */
@EActivity(R.layout.activity_donation)
@OptionsMenu(R.menu.help)
@SuppressLint("Registered")
public class PayPalDonationActivity extends AppCompatActivity {

    @AfterViews
    void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @OptionsItem(android.R.id.home)
    void homeClicked(){
        Intent upIntent = new Intent(this, StreamControlActivity_.class);
        NavUtils.navigateUpTo(this, upIntent);
    }

    @OptionsItem(R.id.mnu_help)
    void helpClicked(){
        Uri metalOnly = Uri.parse(UrlConstants.INSTANCE.getMETAL_ONLY_DONATION_URL());
        Intent homepage = new Intent(Intent.ACTION_VIEW, metalOnly);
        startActivity(homepage);
    }


}
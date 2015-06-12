package com.codingspezis.android.metalonly.player;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;

/**
 * with this activity users can make paypal donations to metal only
 * TODO use androidannotatons
 */
public class PayPalDonationActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_donation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // help button
        // TODO use help menu resource
        MenuItem hlp = menu.add(0, R.id.mnu_help, 0, R.string.menu_help);
        hlp.setIcon(R.drawable.ic_action_web_site);
        hlp.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateUp();
                return true;
            case R.id.mnu_help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showHelp() {
        Uri metalOnly = Uri.parse("http://www.metal-only.de/?action=donation");
        Intent homepage = new Intent(Intent.ACTION_VIEW, metalOnly);
        startActivity(homepage);
    }

    private void navigateUp() {
        Intent upIntent = new Intent(this, StreamControlActivity.class);
        NavUtils.navigateUpTo(this, upIntent);
    }

}
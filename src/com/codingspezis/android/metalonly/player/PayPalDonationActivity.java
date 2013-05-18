package com.codingspezis.android.metalonly.player;

import android.os.*;

import com.actionbarsherlock.app.*;

/**
 * 
 * with this activity users can make paypal donations to metal only
 * 
 */
public class PayPalDonationActivity extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_donation);
	}

}
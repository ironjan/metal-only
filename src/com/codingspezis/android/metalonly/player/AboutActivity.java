package com.codingspezis.android.metalonly.player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * <b>AboutActivity</b><br/>
 * 
 * this activity shows information about the application e.g. codingspezis.com and used software
 * 
 * @author codingspezis.com
 * @version Apr 2, 2013
 *
 */
public class AboutActivity extends SubActivity implements OnClickListener{

	// metal only
	private TextView buttonMetalOnly;
	
	// codingspezis
	private Button   buttonCodingspezisMail;
	private TextView buttonCodingspezisPage;
	
	// opencore
	private TextView buttonOpencore;
	private TextView buttonAPL2Opencore;
	
	// aacdecoder-android
	private TextView buttonDecoder;
	private TextView buttonLGPL;
	
	private TextView buttonSherlock;
	private TextView buttonAPL2Sherlock;
	
	// lazylist
	private TextView buttonLazyList;
	private TextView buttonMIT;
		
	// tutorial
	private TextView buttonTutorial;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setUpButtons();
	}
	
	/**
	 * setup for button references and listeners
	 */
	private void setUpButtons(){
		buttonMetalOnly = (TextView)findViewById(R.id.metalonly);
		buttonMetalOnly.setOnClickListener(this);
		buttonCodingspezisMail = (Button)findViewById(R.id.mailus);
		buttonCodingspezisMail.setOnClickListener(this);
		buttonCodingspezisPage = (TextView)findViewById(R.id.codingspezis);
		buttonCodingspezisPage.setOnClickListener(this);
		buttonOpencore = (TextView)findViewById(R.id.opencore);
		buttonOpencore.setOnClickListener(this);
		buttonAPL2Opencore = (TextView)findViewById(R.id.apl2_opencore);
		buttonAPL2Opencore.setOnClickListener(this);
		buttonDecoder = (TextView)findViewById(R.id.aacdecoder);
		buttonDecoder.setOnClickListener(this);
		buttonLGPL = (TextView)findViewById(R.id.lgpl);
		buttonLGPL.setOnClickListener(this);
		buttonSherlock = (TextView)findViewById(R.id.sherlock);
		buttonSherlock.setOnClickListener(this);
		buttonAPL2Sherlock = (TextView)findViewById(R.id.apl2_sherlock);
		buttonAPL2Sherlock.setOnClickListener(this);
		buttonLazyList = (TextView)findViewById(R.id.lazylist);
		buttonLazyList.setOnClickListener(this);
		buttonMIT = (TextView)findViewById(R.id.mit);
		buttonMIT.setOnClickListener(this);
		buttonTutorial = (TextView)findViewById(R.id.tutorial);
		buttonTutorial.setOnClickListener(this);
	}

	/**
	 * sends system intent ACTION_VIEW (open browser)
	 * @param URL browser opens this URL
	 */
	private void openWebsite(String URL){
		Uri metalOnly = Uri.parse(URL);
		Intent homepage = new Intent(Intent.ACTION_VIEW, metalOnly);
		startActivity(homepage);
	}
	
	/**
	 * sends system intent ACTION_SEND (send mail)
	 * @param strTo receiver of mail
	 * @param strSubject subject of mail
	 * @param strText text of mail
	 */
	private void sendEmail(String strTo, String strSubject, String strText){
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/plain");
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, 	new String[]{strTo});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, 	strSubject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, 	strText);
		startActivity(Intent.createChooser(emailIntent, strTo));
	}
	
	/**
	 * displays specified license
	 * @param license license to display
	 */
	private void displayLicense(String license){
		Intent licenseIntent = new Intent(getApplicationContext(), LicenseActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(LicenseActivity.KEY_BU_LICENSE_NAME, license);
		licenseIntent.putExtras(bundle);
		startActivity(licenseIntent);
	}
	
	@Override
	public void onClick(View v) {
		if(v == buttonMetalOnly){
			openWebsite(getString(R.string.url_metalonly));
		}
		if(v == buttonCodingspezisMail){
			sendEmail(getString(R.string.mailaddress_codingspezis), "", "");
		}
		else if(v == buttonCodingspezisPage){
			openWebsite(getString(R.string.url_codingspezis));
		}
		else if(v == buttonOpencore){
			openWebsite(getString(R.string.url_opencore));
		}
		else if(v == buttonAPL2Opencore){
			displayLicense(LicenseActivity.KEY_BU_LICENSE_APACHE);
		}
		else if(v == buttonDecoder){
			openWebsite(getString(R.string.url_aacdecoder));
		}
		else if(v == buttonLGPL){
			displayLicense(LicenseActivity.KEY_BU_LICENSE_LGPL);
		}
		else if(v == buttonSherlock){
			openWebsite(getString(R.string.url_sherlock));
		}
		else if(v == buttonAPL2Sherlock){
			displayLicense(LicenseActivity.KEY_BU_LICENSE_APACHE);
		}
		else if(v == buttonLazyList){
			openWebsite(getString(R.string.url_lazylist));
		}
		else if(v == buttonMIT){
			displayLicense(LicenseActivity.KEY_BU_LICENSE_MIT);
		}
		else if(v == buttonTutorial){
			openWebsite(getString(R.string.url_androidhive));
		}
	}
	
}

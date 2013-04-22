package com.codingspezis.android.metalonly.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

/**
 * SettingsActivity
 * @version 24.02.2013
 *
 * user can access settings with this class
 *
 */
public class SettingsActivity extends PrefActivity implements OnPreferenceChangeListener{
	
	public static final int DEFAULT_RATE =   0;
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    
	    PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(getString(R.string.app_name));
        prefMgr.setSharedPreferencesMode(MODE_MULTI_PROCESS);
	    
		addPreferencesFromResource(R.xml.preferences);
		Preference pref = findPreference(getString(R.string.settings_key_rate));
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_MULTI_PROCESS);
		try{
			pref.setSummary(prefs.getString(getString(R.string.settings_key_rate),
							getResources().getStringArray(R.array.rate_label)[DEFAULT_RATE]));		
		}catch(Exception e){}
		pref.setOnPreferenceChangeListener(this);
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference instanceof ListPreference && newValue instanceof String)
			preference.setSummary((String)newValue);
		return true;
	}
	
}

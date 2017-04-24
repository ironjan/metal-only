package com.codingspezis.android.metalonly.player.wish;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface WishPrefs {
    @DefaultString("")
    String nick();
    @DefaultString("")
    String artist();
    @DefaultString("")
    String title();
    @DefaultString("")
    String greeting();
}

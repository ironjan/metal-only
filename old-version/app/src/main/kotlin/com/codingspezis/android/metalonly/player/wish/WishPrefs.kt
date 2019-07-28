package com.codingspezis.android.metalonly.player.wish

import org.androidannotations.annotations.sharedpreferences.DefaultString
import org.androidannotations.annotations.sharedpreferences.SharedPref

@SharedPref
interface WishPrefs {
    @DefaultString("")
    fun nick(): String

    @DefaultString("")
    fun artist(): String

    @DefaultString("")
    fun title(): String

    @DefaultString("")
    fun greeting(): String
}

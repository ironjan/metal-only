package com.codingspezis.android.metalonly.player.crashlytics

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean
import org.androidannotations.annotations.sharedpreferences.SharedPref

/**
 * User settings for sending crash reports. Use with the `@Pref CrashlyticsPrefs_`.
 */
@SharedPref
interface CrashlyticsPrefs {
    /**
     * @return `true`, if the user disabled sending crash reports. `false` on default.
     */
    @get:DefaultBoolean(false)
    val isCrashlyticsDisabled: Boolean
}

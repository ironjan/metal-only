package com.codingspezis.android.metalonly.player.crashlytics

import android.content.Context
import com.codingspezis.android.metalonly.player.BuildConfig
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import org.androidannotations.annotations.sharedpreferences.Pref

/**
 * Use this in the main activity to initialize Crashlytics.
 */
@EBean
open class CrashlyticsInitializer {
    @JvmField
    @RootContext
    internal var context: Context? = null

    @JvmField
    @Pref
    internal var prefs: CrashlyticsPrefs_? = null

    fun init() {
        val isDisabledByUser = prefs!!.isCrashlyticsDisabled.get()

        val isDisabled = BuildConfig.DEBUG || isDisabledByUser!!

        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(isDisabled).build())
                .build()
        Fabric.with(context, crashlyticsKit)
    }
}

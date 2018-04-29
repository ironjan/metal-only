package com.github.ironjan.metalonly.client_library

import android.net.ConnectivityManager
import com.hypertrack.hyperlog.HyperLog
import org.androidannotations.annotations.AfterInject
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.SystemService
import org.androidannotations.rest.spring.annotations.RestService
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.OkHttpClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClientException

/**
 * A wrapper around the Rest-Api implementation to adapt its settings. The REST API should not be
 * used directly.
 */
@EBean(scope = EBean.Scope.Singleton)
open class MetalOnlyClientImplementation : MetalOnlyClient {

    @JvmField
    @RestService
    internal var api: MetalOnlyAPI? = null

    @JvmField
    @SystemService
    internal var cm: ConnectivityManager? = null

    private val TAG: String = "MetalOnlyClientImplementation"

    @AfterInject
    internal fun adaptApiSettings() {
        HyperLog.d(TAG, "adaptApiSettings() - started")
        val requestFactory = OkHttpClientHttpRequestFactory()
        requestFactory.setConnectTimeout(TIME_OUT)
        requestFactory.setReadTimeout(TIME_OUT)

        api!!.restTemplate.requestFactory = requestFactory

        disableKeepAlive()

        HyperLog.d(TAG, "adaptApiSettings() - completed")
    }

    private fun disableKeepAlive() {
        System.setProperty("http.keepAlive", "false")
    }

    override fun getStats(): Stats? {
        checkConnectivity()
        return api?.stats
    }

    @Throws(RestClientException::class, NoInternetException::class)
    override fun getPlan(): Plan? {
        checkConnectivity()
        return api?.plan
    }

    override fun getTrack(): TrackWrapper? {
        checkConnectivity()
        return api?.track
    }

    private fun checkConnectivity() {
        HyperLog.d(TAG, "checkConnectivity() - started")
        if (hasNoInternetConnection()) {
            HyperLog.d(TAG, "checkConnectivity() - no connection, throwing NoInternetException")
            throw NoInternetException()
        }
    }

    /**
     * Checks if the device has **no** internet connection.

     * @return `true`, if the phone is not connected to the internet.
     */
    private fun hasNoInternetConnection(): Boolean {
        val hasConnection: Boolean
        val wifiNetwork = cm!!
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileNetwork = cm!!
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiNetwork != null && wifiNetwork.isConnected) {
            hasConnection = true
        } else if (mobileNetwork != null && mobileNetwork.isConnected) {
            hasConnection = true
        } else {
            val activeNetwork = cm!!.activeNetworkInfo
            hasConnection = activeNetwork != null && activeNetwork.isConnected
        }

        return !hasConnection
    }

    companion object {

        private val TIME_OUT = 30 * 1000

    }

}


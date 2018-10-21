package com.github.ironjan.metalonly.client_library

import android.net.ConnectivityManager
import android.util.Log
import arrow.core.Either
import arrow.core.Either.Companion
import com.github.ironjan.metalonly.client_library.model.Track
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import org.androidannotations.annotations.AfterInject
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.SystemService
import org.androidannotations.rest.spring.annotations.RestService
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
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

    @AfterInject
    internal fun adaptApiSettings() {
        changeTimeout()
        disableKeepAlive()
    }

    private fun changeTimeout() {
        val requestFactory = api!!.restTemplate.requestFactory

        if (requestFactory is SimpleClientHttpRequestFactory) {
            val factory = requestFactory

            factory.setConnectTimeout(TIME_OUT)
            factory.setReadTimeout(TIME_OUT)
        } else if (requestFactory is HttpComponentsClientHttpRequestFactory) {
            val factory = requestFactory

            factory.setReadTimeout(TIME_OUT)
            factory.setConnectTimeout(TIME_OUT)
        }
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
        if (hasNoInternetConnection()) {
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

    override fun getTrackV2(): Either<Track, String> {
        "https://api.github.com/repos/kittinunf/Fuel/issues/1".httpGet()
                .responseObject(Track.Deserializer()) { request, _, result ->
                    Log.d("MO CI", request.toString())
                    // return update(result)
                }

        FuelManager.instance.basePath = "http://mensaupb.herokuapp.com/metalonly"

        val (_, _, result) = "/stats".httpGet().responseObject(Track.Deserializer())

        val (data, error) = result

        return if (error == null) {
            Either.left(data!!)
        } else {
            Either.right(error.localizedMessage)
        }
    }

    companion object {

        private val TIME_OUT = 30 * 1000

    }

}


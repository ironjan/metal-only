package com.codingspezis.android.metalonly.player.utils.jsonapi;

import android.net.*;
import android.util.*;

import org.androidannotations.annotations.*;
import org.androidannotations.annotations.rest.*;
import org.androidannotations.api.rest.*;
import org.springframework.http.client.*;
import org.springframework.web.client.*;

@EBean(scope = EBean.Scope.Singleton)
public class MetalOnlyAPIWrapper implements MetalOnlyAPI {

    private static final int TIME_OUT = 30 * 1000;

    private static final String TAG = MetalOnlyAPIWrapper.class.getSimpleName();

    @RestService
    MetalOnlyAPI api;

    @SystemService
    ConnectivityManager cm;

    @Bean
    MyErrorHandler myErrorHandler;

    @AfterInject
    void adaptSettings() {
        changeTimeout();
        disableKeepAlive();
        addErrorHandler();
    }

    private void changeTimeout() {
        final ClientHttpRequestFactory requestFactory = api.getRestTemplate()
                .getRequestFactory();
        if (requestFactory instanceof SimpleClientHttpRequestFactory) {
            Log.d("HTTP", "HttpUrlConnection is used");
            ((SimpleClientHttpRequestFactory) requestFactory)
                    .setConnectTimeout(TIME_OUT);
            ((SimpleClientHttpRequestFactory) requestFactory)
                    .setReadTimeout(TIME_OUT);
        } else if (requestFactory instanceof HttpComponentsClientHttpRequestFactory) {
            Log.d("HTTP", "HttpClient is used");
            ((HttpComponentsClientHttpRequestFactory) requestFactory)
                    .setReadTimeout(TIME_OUT);
            ((HttpComponentsClientHttpRequestFactory) requestFactory)
                    .setConnectTimeout(TIME_OUT);
        }
    }

    private void disableKeepAlive() {
        System.setProperty("http.keepAlive", "false");
    }

    private void addErrorHandler() {
        api.setRestErrorHandler(myErrorHandler);
    }

    @Override
    public Stats getStats() {
        checkConnectivity();
        Stats stats = null;
        try {
            stats = api.getStats();
        } catch (RestClientException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return stats;
    }

    @Override
    public Plan getPlan() {
        checkConnectivity();
        Plan plan = null;
        try {
            plan = api.getPlan();
        } catch (RestClientException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return plan;
    }

    @Override
    public PlanWithStats getPlanWithStats() {
        checkConnectivity();
        PlanWithStats planWithStats = null;
        try {
            planWithStats = api.getPlanWithStats();
        } catch (RestClientException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return planWithStats;
    }

    private void checkConnectivity() {
        if (!hasConnection()) {
            throw new NoInternetException();
        }
    }

    /**
     * Checks if the device has Internet connection.
     *
     * @return <code>true</code> if the phone is connected to the Internet.
     */
    public boolean hasConnection() {
        final NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        final NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

    /**
     * Do not use this method.
     */
    @Override
    public RestTemplate getRestTemplate() {
        return null;
    }

    /**
     * Do not use this method.
     */
    @Override
    public void setRestTemplate(final RestTemplate restTemplate) {
        return;
    }

    /**
     * Do not use this method.
     */
    @Override
    public void setRestErrorHandler(RestErrorHandler handler) {
        return;
    }
}

package com.codingspezis.android.metalonly.player.utils.jsonapi;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.rest.spring.api.RestErrorHandler;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * A wrapper aroung the Rest-Api implementation to catch exceptions etc.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MetalOnlyAPIWrapper implements MetalOnlyAPI, WishGreetAPI {

    private static final int TIME_OUT = 30 * 1000;

    private static final String TAG = MetalOnlyAPIWrapper.class.getSimpleName();

    @RestService
    MetalOnlyAPI api;

    @RestService
    WishGreetAPI wishGreetAPI;

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

        Stats result = Stats.getDefault();
        try {
            Stats apiStats = api.getStats();
            result = (apiStats != null) ? apiStats : result;
        } catch (RestClientException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Plan getPlan() {
        checkConnectivity();
        Plan plan = Plan.getDefault();
        try {
            Plan apiPlan = api.getPlan();
            plan = (apiPlan != null) ? apiPlan : plan;
        } catch (RestClientException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return plan;
    }

    @Override
    public PlanWithStats getPlanWithStats() {
        checkConnectivity();
        PlanWithStats planWithStats = PlanWithStats.getDefault();
        try {
            PlanWithStats apiPlanWithStats = api.getPlanWithStats();
            planWithStats = (apiPlanWithStats != null) ? apiPlanWithStats : planWithStats;
        } catch (RestClientException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return planWithStats;
    }

    @Override
    public String postWishAndGreetings(String nick, String artist, String song, String greet) {
        checkConnectivity();
        String response = wishGreetAPI.postWishAndGreetings(nick, artist, song, greet);
        return cleanWishGreetResponse(response);
    }

    @Override
    public String postGreetings(String nick, String greet) {
        checkConnectivity();
        String response = wishGreetAPI.postGreetings(nick, greet);
        return cleanWishGreetResponse(response);
    }
/*


            if (!TextUtils.isEmpty(editNick.getText())) {
                pairs.add(new BasicNameValuePair("nick", editNick.getText()
                        .toString()));
            }
            if (!TextUtils.isEmpty(editArtist.getText())
                    && editArtist.isEnabled()) {
                pairs.add(new BasicNameValuePair("artist", editArtist.getText()
                        .toString()));
            }
            if (!TextUtils.isEmpty(editTitle.getText())
                    && editTitle.isEnabled()) {
                pairs.add(new BasicNameValuePair("song", editTitle.getText()
                        .toString()));
            }
            if (!TextUtils.isEmpty(editRegard.getText())) {
                pairs.add(new BasicNameValuePair("greet", editRegard.getText()
                        .toString()));
            }
 */
    private String cleanWishGreetResponse(String response){
        if(response == null){
            return "Übermittlung fehlgeschlagen";
        }
        else if (response.contains("Wunsch/Gruss hinzugefügt.")){
            return "Wunsch/Gruss hinzugefügt.";
        }else if(response.contains("Bitte Wunsch/Gruss und einen Nick angeben")){
            return "Bitte Wunsch/Gruss und einen Nick angeben";
        }else {
            return "Wunsch/Gruss wurde übermittelt.";
        }
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
        return activeNetwork != null && activeNetwork.isConnected();

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
        /* Ignore all calls */
    }

    /**
     * Do not use this method.
     */
    @Override
    public void setRestErrorHandler(RestErrorHandler handler) {
        /* Ignore all calls */
    }
}

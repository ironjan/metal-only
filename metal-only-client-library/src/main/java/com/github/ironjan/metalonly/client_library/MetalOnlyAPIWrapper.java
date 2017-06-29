package com.github.ironjan.metalonly.client_library;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.rest.spring.api.RestErrorHandler;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * A wrapper around the Rest-Api implementation to adapt its settings. The REST API should not be
 * used directly.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MetalOnlyAPIWrapper implements MetalOnlyAPI, WishGreetAPI, DemoInterface {

    private static final int TIME_OUT = 30 * 1000;

    private static final String TAG = MetalOnlyAPIWrapper.class.getSimpleName();

    @RestService
    MetalOnlyAPI api;

    @RestService
    WishGreetAPI wishGreetAPI;

    @SystemService
    ConnectivityManager cm;

    @AfterInject
    void adaptApiSettings() {
        changeTimeout();
        disableKeepAlive();
    }

    private void changeTimeout() {
        final ClientHttpRequestFactory requestFactory = api.getRestTemplate().getRequestFactory();

        if (requestFactory instanceof SimpleClientHttpRequestFactory) {
            SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) requestFactory;

            factory.setConnectTimeout(TIME_OUT);
            factory.setReadTimeout(TIME_OUT);
        } else if (requestFactory instanceof HttpComponentsClientHttpRequestFactory) {
            HttpComponentsClientHttpRequestFactory factory = (HttpComponentsClientHttpRequestFactory) requestFactory;

            factory.setReadTimeout(TIME_OUT);
            factory.setConnectTimeout(TIME_OUT);
        }
    }

    private void disableKeepAlive() {
        System.setProperty("http.keepAlive", "false");
    }

    /**
     * @deprecated The wrapper seemed like a nice idea - but catching all exceptions leads to
     * undefined/unwanted behaviour in the calling classes. The wrapper should not be used anymore
     * to call API methods.
     */
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

    /**
     * @return the plan or null
     * @throws NoInternetException if no internet connection is present
     * @throws RestClientException rethrow of underlying API implementation exception
     */
    @Override
    public Plan getPlan() throws RestClientException, NoInternetException {
        checkConnectivity();
        return api.getPlan();
    }

    /**
     * @deprecated The wrapper seemed like a nice idea - but catching all exceptions leads to
     * undefined/unwanted behaviour in the calling classes. The wrapper should not be used anymore
     * to call API methods.
     */
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
    public TrackWrapper getTrack() {
        checkConnectivity();
        return api.getTrack();
    }

    /**
     * @deprecated The wrapper seemed like a nice idea - but catching all exceptions leads to
     * undefined/unwanted behaviour in the calling classes. The wrapper should not be used anymore
     * to call API methods.
     */
    @Override
    public String postWishAndGreetings(String nick, String artist, String song, String greet) {
        checkConnectivity();
        String response = wishGreetAPI.postWishAndGreetings(nick, artist, song, greet);
        return cleanWishGreetResponse(response);
    }

    /**
     * @deprecated The wrapper seemed like a nice idea - but catching all exceptions leads to
     * undefined/unwanted behaviour in the calling classes. The wrapper should not be used anymore
     * to call API methods.
     */
    @Override
    public String postGreetings(String nick, String greet) {
        checkConnectivity();
        String response = wishGreetAPI.postGreetings(nick, greet);
        return cleanWishGreetResponse(response);
    }

    private String cleanWishGreetResponse(String response) {
        if (response == null) {
            return "Übermittlung fehlgeschlagen";
        } else if (response.contains("Wunsch/Gruss hinzugefügt.")) {
            return "Wunsch/Gruss hinzugefügt.";
        } else if (response.contains("Bitte Wunsch/Gruss und einen Nick angeben")) {
            return "Bitte Wunsch/Gruss und einen Nick angeben";
        } else {
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
     * Checks if the device has <b>no</b> internet connection.
     *
     * @return <code>true</code>, if the phone is not connected to the internet.
     */
    public boolean hasNoInternetConnection() {
        return !hasConnection();
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

package com.codingspezis.android.metalonly.player.siteparser;

import com.codingspezis.android.metalonly.player.StreamControlActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class HTTPDownloadImplementation {
    private boolean canceled;
    private boolean timedout;
    private final String url;
    private final OnHTTPGrabberListener listener;
    private final long timeoutDelay;

    private HTTPDownloadImplementation(String url, OnHTTPGrabberListener listener, long timeoutDelay) {
        this.url = url;
        this.listener = listener;
        this.timeoutDelay = timeoutDelay;
    }

    public void download() {
        boolean timedout = false;
        Timer timeoutTimer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!canceled) {
                    timeout();
                    if (listener != null) {
                        listener.onTimeout();
                    }
                }
            }
        };
        timeoutTimer.schedule(timerTask, timeoutDelay);
        // HTTP part
        URLConnection con;
        try {
            con = (new URL(url)).openConnection();
            con.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            if (!canceled && !timedout) {
                timeoutTimer.cancel();
                if (listener != null) {
                    listener.onSuccess(reader);
                }
            }
            reader.close();
        } catch (Exception e) {
            canceled = true;
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }
    }

    private void timeout() {
        timedout = true;
    }

    public static HTTPDownloadImplementation instance(String url, OnHTTPGrabberListener listener, long timeoutDelay) {
        return new HTTPDownloadImplementation(url, listener, timeoutDelay);
    }
}

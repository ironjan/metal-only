package com.codingspezis.android.metalonly.player.plan;

import android.content.Context;

import com.codingspezis.android.metalonly.player.PlanActivity_;
import com.codingspezis.android.metalonly.player.StreamControlActivity;
import com.codingspezis.android.metalonly.player.siteparser.HTTPGrabber;
import com.codingspezis.android.metalonly.player.siteparser.OnHTTPGrabberListener;

import java.io.BufferedReader;

public class PlanGrabber {

    private final StreamControlActivity streamControlActivity;

    private final Context context;

    private final HTTPGrabber grabber;
    private final OnHTTPGrabberListener listener = new OnHTTPGrabberListener() {

        @Override
        public void onSuccess(BufferedReader httpResponse) {
            // start activity
            try {
                String site = "", line;
                while ((line = httpResponse.readLine()) != null) {
                    site += line;
                }

                PlanActivity_.intent(context).site(site).start();
            } catch (Exception e) {
                StreamControlActivity.toastMessage(context, e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onTimeout() {
        }

        @Override
        public void onError(String error) {
        }

        @Override
        public void onCancel() {
        }

    };

    /**
     * Constructor
     */
    public PlanGrabber(StreamControlActivity streamControlActivity, Context context, String URL) {
        this.streamControlActivity = streamControlActivity;
        this.context = context;
        grabber = new HTTPGrabber(context, URL, listener);
    }

    public void start() {
        grabber.start();
    }

}
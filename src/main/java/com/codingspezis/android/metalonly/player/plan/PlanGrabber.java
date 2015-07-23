package com.codingspezis.android.metalonly.player.plan;

import android.content.*;
import android.os.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.siteparser.*;

import java.io.*;

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
                Bundle bundle = new Bundle();
                bundle.putString(PlanActivity.KEY_SITE, site);
                Intent planIntent = PlanActivity_.intent(context.getApplicationContext()).get();
                planIntent.putExtras(bundle);
                PlanGrabber.this.streamControlActivity.startActivity(planIntent);
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
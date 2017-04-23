package com.codingspezis.android.metalonly.player.plan;

import android.content.Context;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.siteparser.HTTPGrabber;
import com.codingspezis.android.metalonly.player.siteparser.OnHTTPGrabberListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;

public class PlanGrabber {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

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

                planGrabberCallback.onPlanLoadSuccess(site);
            } catch (Exception e) {
                planGrabberCallback.onPlanLoadError(e.getMessage());
            }
        }

        @Override
        public void onTimeout() {
            planGrabberCallback.onPlanLoadError(R.string.plan_timeout);
        }

        @Override
        public void onError(String error) {
            planGrabberCallback.onPlanLoadError(error);
        }

        @Override
        public void onCancel() {
            planGrabberCallback.onPlanLoadError(R.string.plan_load_cancelled);
        }

    };
    private final PlanGrabberCallback planGrabberCallback;

    /**
     * Constructor
     */
    public PlanGrabber(Context context, String URL) {
        PlanGrabberCallback defaultCallback = new PlanGrabberCallback() {
            @Override
            public void onPlanLoadSuccess(String site) {
                LOGGER.error("Default implementation!");
            }

            @Override
            public void onPlanLoadError(String message) {
                LOGGER.error("Default implementation!");
            }

            @Override
            public void onPlanLoadError(int stringId) {
                LOGGER.error("Default implementation!");
            }
        };
        if(context instanceof PlanGrabberCallback){
            planGrabberCallback = (PlanGrabberCallback) context;
        }else {
            this.planGrabberCallback = defaultCallback;
        }

        grabber = new HTTPGrabber(context, URL, listener);
    }


    public void start() {
        grabber.start();
    }

    public interface PlanGrabberCallback{
        void onPlanLoadSuccess(String site);

        void onPlanLoadError(String message);

        void onPlanLoadError(int stringId);
    }
}
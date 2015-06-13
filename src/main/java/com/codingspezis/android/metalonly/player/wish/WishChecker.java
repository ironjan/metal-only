package com.codingspezis.android.metalonly.player.wish;

import android.content.*;

import com.codingspezis.android.metalonly.player.siteparser.*;

import java.io.*;

/**
 * checks for open wishes / regards on wish page on metal-only.de
 */
public class WishChecker {

    private final HTTPGrabber grabber;
    private OnWishesCheckedListener wishListener;
    private final OnHTTPGrabberListener grabberListener =

            new OnHTTPGrabberListener() {

                @Override
                public void onSuccess(BufferedReader httpResponse) {
                    AllowedActions allowedActions = new AllowedActions();
                    allowedActions.regards = true;
                    allowedActions.wishes = true;
                    allowedActions.moderated = true;
                    try {
                        String line = httpResponse.readLine();
                        while (line != null) {
                            // format:
                            // "Derzeitiges Limit: 4 Wünsche und 3 Grüße pro Hörer"
                            if (line.contains("Derzeitiges Limit:")) {
                                try {
                                    allowedActions.limit = line.substring(
                                            line.indexOf("Derzeitiges Limit:"),
                                            line.indexOf("pro Hörer")).trim();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (line.contains("sind keine Grüße möglich.")) {
                                allowedActions.regards = false;
                            }
                            if (line.contains("die Playlist ist bereits voll")) {
                                allowedActions.wishes = false;
                            }
                            if (line.contains("sind nur in der moderierten Sendezeit möglich!")
                                    || line.contains("Aktuell On Air: MetalHead")) {
                                allowedActions.regards = false;
                                allowedActions.wishes = false;
                                allowedActions.moderated = false;
                                break;
                            }
                            line = httpResponse.readLine();
                        }
                        if (wishListener != null) {
                            wishListener.onWishesChecked(allowedActions);
                        }

                    } catch (Exception e) {
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

    public WishChecker(Context context, String URL) {
        grabber = new HTTPGrabber(context, URL, grabberListener);
    }

    public void start() {
        grabber.start();
    }

    /**
     * settings OnWishesCheckedListener
     *
     * @param listener OnWishesCheckedListener
     */
    public void setOnWishesCheckedListener(OnWishesCheckedListener listener) {
        wishListener = listener;
    }

}

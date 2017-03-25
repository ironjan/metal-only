package com.codingspezis.android.metalonly.player.wish;

import android.content.Context;

import com.codingspezis.android.metalonly.player.siteparser.HTTPGrabber;
import com.codingspezis.android.metalonly.player.siteparser.OnHTTPGrabberListener;

import java.io.BufferedReader;

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
                    allowedActions.setRegards(true);
                    allowedActions.setWishes(true);
                    allowedActions.setModerated(true);
                    try {
                        String line = httpResponse.readLine();
                        while (line != null) {
                            // format:
                            // "Derzeitiges Limit: 4 Wünsche und 3 Grüße pro Hörer"
                            if (line.contains("Derzeitiges Limit:")) {
                                try {
                                    allowedActions.setLimit(line.substring(
                                            line.indexOf("Derzeitiges Limit:"),
                                            line.indexOf("pro Hörer")).trim());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (line.contains("sind keine Grüße möglich.")) {
                                allowedActions.setRegards(false);
                            }
                            if (line.contains("die Playlist ist bereits voll")) {
                                allowedActions.setWishes(false);
                            }
                            if (line.contains("sind nur in der moderierten Sendezeit möglich!")
                                    || line.contains("Aktuell On Air: MetalHead")) {
                                allowedActions.setRegards(false);
                                allowedActions.setWishes(false);
                                allowedActions.setModerated(false);
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

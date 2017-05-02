package com.codingspezis.android.metalonly.player.siteparser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.StreamControlActivity;

import java.net.URL;

/**
 * this class is for sending HTTP GET & receiving response<br/>
 * while communicating it displays a progress dialog<br/>
 * use {@link OnHTTPGrabberListener} to handle events
 */
public class HTTPGrabber extends Thread {

    private final long timeoutDelay = 5000;
    protected Context context;
    protected String URL;
    protected OnHTTPGrabberListener listener;
    protected Handler handler;
    private ProgressDialog progressDialog;

    /**
     * constructor (does not communicate)
     *
     * @param context  GET context
     * @param URL      GET URL
     * @param listener this listener allows to handle GET response
     */
    public HTTPGrabber(Context context, String URL,
                       OnHTTPGrabberListener listener) {
        this.context = context;
        this.URL = URL;
        this.listener = listener;
        handler = new Handler();
    }

    /**
     * if there is no network connection available this method shows network
     * settings
     *
     * @param context GET context
     * @return true if network settings will be displayed - false otherwise
     */
    public static boolean displayNetworkSettingsIfNeeded(final Context context) {
        if (isOnline(context)) {
            return false;
        } else {
            (new Handler(context.getMainLooper())).post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle(R.string.no_internet);
                    alert.setMessage(R.string.open_internet_settings);
                    alert.setNegativeButton(R.string.no, null);
                    alert.setPositiveButton(R.string.yes,
                            new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent tmpIntent = new Intent(
                                            Intent.ACTION_MAIN);
                                    tmpIntent
                                            .setAction("android.settings.WIRELESS_SETTINGS");
                                    context.startActivity(tmpIntent);
                                }
                            }
                    );
                    alert.show();
                }
            });
            return true;
        }
    }

    /**
     * checks network connection
     *
     * @return true if there is a network connection false otherwise
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
    }

    @Override
    public void run() {
        HTTPDownloadImplementation.instance(URL, listener, timeoutDelay).download();
    }

    /**
     * this function shows or hides progress dialog
     *
     * @param visible visible or not
     */
    protected void setProgressDialogVisible(final boolean visible) {
        Runnable progressRun = new Runnable() {
            @Override
            public void run() {
                if (visible) {
                    progressDialog = ProgressDialog.show(context, "",
                            context.getString(R.string.communicating), true,
                            true, new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    if (listener != null) {
                                        listener.onCancel();
                                    }
                                }
                            }
                    );
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        };
        handler.post(progressRun);
    }

    /**
     * displays timeout message
     */
    protected void displayTimeoutMSG() {
        StreamControlActivity.toastMessage(context, context.getString(R.string.timeout));
    }

}

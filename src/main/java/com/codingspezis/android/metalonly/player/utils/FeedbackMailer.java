package com.codingspezis.android.metalonly.player.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.codingspezis.android.metalonly.player.BuildConfig;
import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.StreamControlActivity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.res.StringRes;

@EBean
public class FeedbackMailer {
    @RootContext
    Context context;

    @StringRes
    String app_name;

    @StringRes
    String mailaddress_codingspezis;


    /**
     * sends system intent ACTION_SEND (send mail)
     */
    public void sendEmail() {
        String subject = "[" + app_name + " " + BuildConfig.VERSION_NAME
                + "] Feedback, Fehler";

        String body = "\n\n\n---\n" + app_name + "\nVersion: " + BuildConfig.VERSION_NAME + "\n" +
                "Android: " + Build.VERSION.RELEASE + "\n" +
                "Model: " + Build.MODEL;


        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailaddress_codingspezis});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            context.startActivity(Intent.createChooser(emailIntent, mailaddress_codingspezis));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(R.string.no_mail_app);
            toast.show();
        }
    }
}

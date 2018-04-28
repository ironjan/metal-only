package com.codingspezis.android.metalonly.player.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.codingspezis.android.metalonly.player.BuildConfig
import com.codingspezis.android.metalonly.player.R
import com.hypertrack.hyperlog.HyperLog
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import org.androidannotations.annotations.res.StringRes
import java.io.File

@EBean
open class FeedbackMailer {
    @RootContext
    protected lateinit var context: Context

    @StringRes
    protected lateinit var app_name: String

    @StringRes
    protected lateinit var mailaddress_codingspezis: String

    /**
     * sends system intent ACTION_SEND (send mail)
     */
    fun sendEmail() {
        val subject = "[$app_name ${BuildConfig.VERSION_NAME}] Feedback, Fehler"

        val logfile: File? = HyperLog.getDeviceLogsInFile(context)
        val logFileUri = Uri.fromFile(logfile)

        val body = "\n\n\n---\n" +
                    "$app_name\nVersion: ${BuildConfig.VERSION_NAME}\n" +
                    "Android: ${Build.VERSION.RELEASE}\n" +
                    "Model: ${Build.MODEL}"

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailaddress_codingspezis))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        HyperLog.e("FeedbackMailer", "Sending mail")
        if (logfile != null) {
            HyperLog.e("FeedbackMailer", "Sending mail with attachement")
            emailIntent.putExtra(Intent.EXTRA_STREAM, logFileUri)
        }

        try {
            context.startActivity(Intent.createChooser(emailIntent, mailaddress_codingspezis))
        } catch (ex: android.content.ActivityNotFoundException) {
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.setText(R.string.no_mail_app)
            toast.show()
        }

    }
}

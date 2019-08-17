package de.ironjan.metalonly

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import de.ironjan.metalonly.log.LW
import java.io.File

object Mailer {
    internal fun sendFeedback(context: Context) {
        val subject = String.format("Metal Only Feedback %s", BuildConfig.VERSION_NAME)



        val osVersion = Build.VERSION.RELEASE
        val manufacturer = Build.MANUFACTURER;
        val model = Build.MODEL

        val info = "$manufacturer $model\nAndroid version: $osVersion"
        val logs = LW.getLogs()
        val body = "Feedback:\n\n\n---\nAdditional Info:\n$info\n\nLogs:\n$logs"



        sendMail(context, subject, body)
    }

    private val developerMails = arrayOf("lippertsjan+mensaupb@gmail.com")

    /**
     * Sends an email with the given subject and body. Notifies the user that an email app should be
     * set up, if there is none available.
     *
     * @param subject The mail's subject
     * @param body The mail's body
     */
    fun sendMail(context: Context, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, developerMails)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)


        val activityExists = intent.resolveActivity(context.packageManager) != null
        if (activityExists) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "No mail app installed.", Toast.LENGTH_LONG).show()
        }
    }
}
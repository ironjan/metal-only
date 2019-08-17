package de.ironjan.metalonly.log

import android.content.Context
import android.util.Log
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.io.FileOutputStream
import com.hypertrack.hyperlog.HyperLog




object LW {
    var applicationContext: Context? = null

    fun `init`(context: Context) {
        val c = context.applicationContext
        applicationContext = c
        HyperLog.initialize(c)
        HyperLog.setLogLevel(Log.VERBOSE)
    }

    fun v(tag: String, msg: String) {
        HyperLog.v(tag, msg)
    }


    fun d(tag: String, msg: String) {
        HyperLog.d(tag, msg)
    }


    fun w(tag: String, msg: String) {
        HyperLog.w(tag, msg)
    }

    fun i(tag: String, msg: String) {
        HyperLog.i(tag, msg)
    }

    fun e(tag: String, msg: String, e: Throwable? = null) {
        if (e != null) {
            HyperLog.e(tag, msg, e)
        } else {
            HyperLog.e(tag, msg)
        }
    }

    fun getLogs(): String {
        val file = HyperLog.getDeviceLogsInFile(applicationContext)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.readText(Charsets.UTF_8)
    }

}

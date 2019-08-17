package de.ironjan.metalonly.log

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import de.ironjan.metalonly.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.io.FileOutputStream


object LW {
    enum class Level { VERBOSE, DEBUG, INFO, WARNING, ERROR }

    private const val LogQueueFlushLimit = 1
    private const val LogFileName = "metalonly.log"
    private const val LogFileMaxSizeInMb = 2

    val q: Queue<String> = ConcurrentLinkedQueue<String>()
    var applicationContext: Context? = null

    fun `init`(context: Context) {
        if (applicationContext == null) {
            applicationContext = context.applicationContext
        }
    }

    fun v(tag: String, msg: String) {
        Log.v(tag, msg)
        internalLog(Level.VERBOSE, tag, msg)
    }


    fun d(tag: String, msg: String) {
        Log.d(tag, msg)
        internalLog(Level.DEBUG, tag, msg)
    }


    fun w(tag: String, msg: String) {
        Log.w(tag, msg)
        internalLog(Level.WARNING, tag, msg)
    }

    fun i(tag: String, msg: String) {
        Log.i(tag, msg)
        internalLog(LW.Level.INFO, tag, msg)
    }

    fun e(tag: String, msg: String, e: Throwable? = null) {
        if (e != null) {
            Log.e(tag, msg, e)
        } else {
            Log.e(tag, msg)
        }
        internalLog(Level.ERROR, tag, msg, e)
    }

    @SuppressLint("SimpleDateFormat")
    private fun internalLog(level: Level, tag: String, msg: String, e: Throwable? = null) {
        val rightNow = Calendar.getInstance() //initialized with the current date and time

        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(rightNow.time)

        val em = if (e != null) "{{{$e}}}" else ""


        val appVersion = BuildConfig.VERSION_NAME

        val logString = "$appVersion: $formattedDate - $level - $tag: $msg $em\n"
        q.add(logString)
        if (q.size > LogQueueFlushLimit) {
            flushQ()
        }
    }

    @Synchronized
    private fun flushQ() {
        if (applicationContext != null) {
            val file = File(applicationContext!!.filesDir, LogFileName)

            val fis = FileOutputStream(file)
            val lock = fis.channel.lock() ?: return // either we get the log or flush later. may loose log entries.

            if (!file.exists()) {
                file.createNewFile()
            }

            val append = q.joinToString("")
            q.clear()

            val fileSizeInMb = file.length() / (64.0 * 1024)
            if (fileSizeInMb > LogFileMaxSizeInMb) {
                // implicitely clear file
                file.writeText(append, Charsets.UTF_8)
            } else {
                file.appendText(append, Charsets.UTF_8)
            }

            lock.release()
        }
    }

    fun getLogs(): String {
        flushQ()
        val file = File(applicationContext!!.filesDir, LogFileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.readText(Charsets.UTF_8)
    }

}

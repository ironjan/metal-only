package de.ironjan.metalonly.log

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

// TODO add writing to file etc.
object LW {
    enum class Level {VERBOSE, DEBUG, INFO, WARNING, ERROR }
    val q: Queue<String> = ConcurrentLinkedQueue<String>()

    fun e(tag: String, msg: String, e: Throwable? = null) {
        if (e != null) {
            Log.e(tag, msg, e)
        } else {
            Log.e(tag, msg)
        }
        internalLog(Level.ERROR, tag, msg, e)
    }

    private fun internalLog(level: Level, tag: String, msg: String, e: Throwable? = null) {
        val rightNow = Calendar.getInstance() //initialized with the current date and time

        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(rightNow.time)

        val em = e?.toString() ?: ""
        val logString = "$formattedDate - $level: $msg {{{$em}}}\n"
        q.add(logString)
        if (q.size > 100) {
           val newQ = q.drop(50)
            q.clear()
            q.addAll(newQ)
            // TODO write to file
        }
    }

    fun d(tag: String, msg: String) {
        Log.d(tag, msg)
        internalLog(Level.DEBUG, tag, msg)
    }

    fun w(tag: String, msg: String) {
        Log.w(tag, msg)
        internalLog(Level.WARNING, tag, msg)
    }
}
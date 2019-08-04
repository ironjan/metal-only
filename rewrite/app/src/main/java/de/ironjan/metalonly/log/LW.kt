package de.ironjan.metalonly.log

import android.util.Log

// TODO add writing to file etc.
object LW {
  fun e(tag: String, msg: String, e: Throwable? = null) {
      if(e != null) {
          Log.e(tag, msg, e)
      }else{
          Log.e(tag, msg)
      }
  }

    fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }
    fun w(tag: String, msg: String) {
        Log.w(tag, msg)
    }
}
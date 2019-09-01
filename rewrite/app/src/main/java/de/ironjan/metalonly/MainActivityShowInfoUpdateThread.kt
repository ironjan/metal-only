package de.ironjan.metalonly

import android.content.Context
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.ShowInfo
import de.ironjan.metalonly.log.LW

class MainActivityShowInfoUpdateThread(private val context: Context, private val callback: OnShowInfoUpdateCallback): Runnable {
    override fun run() {
        val tag = "MainActivity.ShowInfoUpdateThread"
        LW.d(tag, "Prepared show info update thread")
        var lastShowInfo: ShowInfo? = null
        while (callback.IsResumed()) {
            Thread.sleep(5 * 60 * 1000) // start with sleeping because loadStats includes current track
            // Replace this with scheduled service or lookup in plan

            val showInfo = Client(context).getShowInfo()
            if (showInfo.isRight()) {
                showInfo.map {
                    if (it != lastShowInfo) {
                        // broadcast it
                        callback.onShowInfoChange(it)
                        lastShowInfo = it
                        LW.d(tag, "'Broadcasted' show info")
                    }
                }
            }
        }
        LW.d(tag, "Show info update thread is not needed anymore")
    }

    interface OnShowInfoUpdateCallback : Resumable{
        fun onShowInfoChange(showInfo: ShowInfo)
    }
}
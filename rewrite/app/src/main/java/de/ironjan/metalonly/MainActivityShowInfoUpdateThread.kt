package de.ironjan.metalonly

import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.ShowInfo
import de.ironjan.metalonly.log.LW

class MainActivityShowInfoUpdateThread(private val mainActivity: MainActivity): Runnable {
    override fun run() {
        val tag = "MainActivity.ShowInfoUpdateThread"
        LW.d(tag, "Prepared show info update thread")
        var lastShowInfo: ShowInfo? = null
        while (mainActivity.isResumed) {
            Thread.sleep(5 * 60 * 1000) // start with sleeping because loadStats includes current track
            // Replace this with scheduled service or lookup in plan

            val showInfo = Client(mainActivity).getShowInfo()
            if (showInfo.isRight()) {
                showInfo.map {
                    if (it != lastShowInfo) {
                        // broadcast it
                        mainActivity.onShowInfoChange(it)
                        lastShowInfo = it
                        LW.d(tag, "'Broadcasted' show info")
                    }
                }
            }
        }
        LW.d(tag, "Show info update thread is not needed anymore")
    }

    interface OnShowInfoUpdateCallback {
        fun onShowInfoChange(showInfo: ShowInfo)
    }
}
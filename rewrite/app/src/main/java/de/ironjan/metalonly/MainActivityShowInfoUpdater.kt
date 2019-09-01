package de.ironjan.metalonly

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.ShowInfo
import de.ironjan.metalonly.log.LW

class MainActivityShowInfoUpdater(
    private val context: Context,
    private val callback: OnShowInfoUpdateCallback
) : LifecycleObserver {
    @Volatile
    private var isActive = false

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        if(isActive) return

        LW.d(TAG, "onResume event. Starting thread and setting isActive to true.")
        Thread {
            isActive = true
            LW.d(TAG, "Prepared show info update thread")
            var lastShowInfo: ShowInfo? = null
            while (isActive) {
                Thread.sleep(5 * 60 * 1000) // start with sleeping because loadStats includes current track
                // Replace this with scheduled service or lookup in plan

                val showInfo = Client(context).getShowInfo()
                if (showInfo.isRight()) {
                    showInfo.map {
                        if (it != lastShowInfo) {
                            // broadcast it
                            callback.onShowInfoChange(it)
                            lastShowInfo = it
                            LW.d(TAG, "'Broadcasted' show info")
                        }
                    }
                }
            }
            LW.d(TAG, "Show info update thread is not needed anymore")
        }.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        isActive = false
        LW.d(TAG, "onPause event. Setting isActive to false.")
    }

    interface OnShowInfoUpdateCallback : Resumable {
        fun onShowInfoChange(showInfo: ShowInfo)
    }

    companion object {
        private const val TAG = "MainActivity.ShowInfoUpdateThread"
    }
}
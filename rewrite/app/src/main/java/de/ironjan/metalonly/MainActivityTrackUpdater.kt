package de.ironjan.metalonly

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.TrackInfo
import de.ironjan.metalonly.log.LW

class MainActivityTrackUpdater(private val context: Context, private val trackUpdate: TrackUpdate):
    LifecycleObserver {
    @Volatile
    private var isActive = false

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        if(isActive) return

        val tag = TAG + Math.random()
        LW.d(tag, "onResume event. Starting thread and setting isActive to true.")
        Thread{
            isActive = true
            LW.d(tag, "Prepared track info update thread")
            var lastTrackInfo: TrackInfo? = null
            while (isActive) {
                Thread.sleep(30 * 1000) // start with sleeping because loadStats includes current track
                // FIXME is this the best way???

                val track = Client(context).getTrack()
                if (track.isRight()) {
                    track.map {
                        if (it != lastTrackInfo) {
                            // broadcast it
                            trackUpdate.onTrackChange(it)
                            lastTrackInfo = it
                            LW.d(tag, "'Broadcasted' track info")
                        }
                    }
                }
            }
            LW.d(tag, "Track info update thread is not needed anymore")
        }.start()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        isActive = false
        LW.d(TAG, "onPause event. Setting isActive to false.")
    }

    interface TrackUpdate: Resumable{
        fun onTrackChange(trackInfo: TrackInfo)
    }

    companion object {
        private const val TAG = "MainActivityTrackUpdater"
    }
}
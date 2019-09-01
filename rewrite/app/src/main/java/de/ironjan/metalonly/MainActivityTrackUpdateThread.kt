package de.ironjan.metalonly

import android.content.Context
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.TrackInfo
import de.ironjan.metalonly.log.LW

class MainActivityTrackUpdateThread(private val context: Context, private val trackUpdate: TrackUpdate): Runnable {
    override fun run() {
        val tag = "MainActivity.TrackInfoUpdateThread"
        LW.d(tag, "Prepared track info update thread")
        var lastTrackInfo: TrackInfo? = null
        while (trackUpdate.IsResumed()) {
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
    }
    interface TrackUpdate: Resumable{
        fun onTrackChange(trackInfo: TrackInfo)
    }
}
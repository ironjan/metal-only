package de.ironjan.metalonly

import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.TrackInfo
import de.ironjan.metalonly.log.LW

class MainActivityTrackUpdateThread(private val mainActivity: MainActivity): Runnable {
    override fun run() {
        val tag = "MainActivity.TrackInfoUpdateThread"
        LW.d(tag, "Prepared track info update thread")
        var lastTrackInfo: TrackInfo? = null
        while (mainActivity.isResumed) {
            Thread.sleep(30 * 1000) // start with sleeping because loadStats includes current track
            // FIXME is this the best way???

            val track = Client(mainActivity).getTrack()
            if (track.isRight()) {
                track.map {
                    if (it != lastTrackInfo) {
                        // broadcast it
                        mainActivity.onTrackChange(it)
                        lastTrackInfo = it
                        LW.d(tag, "'Broadcasted' track info")
                    }
                }
            }
        }
        LW.d(tag, "Track info update thread is not needed anymore")
    }
    interface TrackUpdate{
        fun onTrackChange(trackInfo: TrackInfo);
    }
}
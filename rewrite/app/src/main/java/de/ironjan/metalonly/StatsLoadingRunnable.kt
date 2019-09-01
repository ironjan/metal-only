package de.ironjan.metalonly

import android.os.AsyncTask
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.Stats
import de.ironjan.metalonly.log.LW

class StatsLoadingRunnable(private val mainActivity: MainActivity): Runnable {
    override fun run() {
        LW.d(TAG, "Loading stats..")
        var attempts = 0
        while (attempts < 4) {
            try {
                val stats = Client(mainActivity).getStats()

                if (stats.isLeft()) {
                    stats.mapLeft {
                        LW.w(TAG, "Loading stats failed: $it")
                        mainActivity.onStatsLoadingError(it)
                    }
                } else {
                    stats.map {
                        mainActivity.onStatsLoadingSuccess(it)
                        return
                    }
                }
            } catch (e: Exception) {
                LW.e(TAG, "Loading stats failed. Attempt $attempts", e)
            }
            attempts += 1
        }
    }
    companion object {
        private const val TAG = "ShowLoadingRunnable"
    }
    interface StatsLoadingCallback{
        fun onStatsLoadingError(s: String)
        fun onStatsLoadingSuccess(stats: Stats)

    }
}
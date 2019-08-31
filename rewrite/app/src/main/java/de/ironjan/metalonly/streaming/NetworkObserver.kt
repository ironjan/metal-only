package de.ironjan.metalonly.streaming

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.net.ConnectivityManagerCompat
import de.ironjan.metalonly.log.LW

class NetworkObserver(private val moStreamingService: MoStreamingService) : BroadcastReceiver() {
    private val connectivityManager =
        moStreamingService.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var lastNetworkType: Int = connectivityManager.activeNetworkInfo.type

    override fun onReceive(context: Context, intent: Intent) {
        val networkInfo =
            ConnectivityManagerCompat.getNetworkInfoFromBroadcast(connectivityManager, intent)
        LW.i(TAG, "Received network change event to: $networkInfo, ${networkInfo?.type}.")

        val type = networkInfo?.type ?: return

        if (type != lastNetworkType) {
            moStreamingService.restartPlayback()
            lastNetworkType = type
            LW.d(TAG, "Updated last known network type.")
        }
    }

    companion object {
        private const val TAG = "NetworkObserver"
    }
}
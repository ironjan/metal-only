package de.ironjan.metalonly.streaming

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkObserver(private val moStreamingService: MoStreamingService): BroadcastReceiver() {
    private val connectivityManager = moStreamingService.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun onReceive(context: Context?, intent: Intent?) {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

    }

    /*
    receive CONNECTIVITY_ACTION broadcasts if they register their BroadcastReceiver with Context.registerReceiver() and
     */

}
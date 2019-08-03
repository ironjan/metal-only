package de.ironjan.metalonly.streaming

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat

class MoStreamingService : Service() {
    val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder? = binder




    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     * See https://developer.android.com/guide/components/bound-services
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): MoStreamingService= this@MoStreamingService
    }
}
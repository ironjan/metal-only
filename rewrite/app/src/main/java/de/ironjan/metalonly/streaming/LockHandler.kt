package de.ironjan.metalonly.streaming

import android.content.Context
import android.net.wifi.WifiManager
import android.os.PowerManager
import de.ironjan.metalonly.log.LW

class LockHandler private constructor(private val context:Context) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val wakeLock: PowerManager.WakeLock
    private val multiCastLock: WifiManager.MulticastLock
    private val wifiLock: WifiManager.WifiLock

    init {
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock")
        LW.i(MoStreamingService.TAG, "Acquired wifilock")


        multiCastLock = wifiManager.createMulticastLock("lockWiFiMulticast")
        multiCastLock.setReferenceCounted(false)
        multiCastLock.acquire()

        LW.i(MoStreamingService.TAG, "Acquired muticastLock")


        wakeLock =
            (context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.packageName + TAG).apply {
                    acquire(TIMEOUT_120_MINUTES /*120 minutes*/)
                }
            }
        LW.i(MoStreamingService.TAG, "Acquired wakelock explictely.")
    }


    fun releaseLocks() {
        if (wifiLock.isHeld) {
            wifiLock.release()
            LW.i(MoStreamingService.TAG, "Released wifilock")
        } else {
            LW.w(MoStreamingService.TAG, "wifilock not yet acquired but releaseLocks() is called.")
        }

        if (multiCastLock.isHeld) {
            multiCastLock.release()
            LW.i(MoStreamingService.TAG, "Released muticastLock")
        } else {
            LW.w(MoStreamingService.TAG, "muticastLock not yet acquired but releaseLocks() is called.")
        }

        if (wakeLock.isHeld) {
            wakeLock.release()
            LW.i(MoStreamingService.TAG, "Released wakelock")
        } else {
            LW.w(MoStreamingService.TAG, "wakeLock not yet acquired but releaseLocks() is called.")
        }
    }

    companion object {
        private const val TIMEOUT_120_MINUTES = 120 * 60 * 1000L
        private const val TAG = "LockHandler"

        fun acquire(context: Context): LockHandler = LockHandler(context)
    }
}
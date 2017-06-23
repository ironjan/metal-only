package com.codingspezis.android.metalonly.player.stream.track_info

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

import com.codingspezis.android.metalonly.player.BuildConfig
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPI_
import com.codingspezis.android.metalonly.player.utils.jsonapi.NoInternetException
import com.codingspezis.android.metalonly.player.utils.jsonapi.Stats

import org.slf4j.LoggerFactory
import org.springframework.web.client.RestClientException

/**
 * A [Runnable] that will fetch the current show information (moderator and track) in regular
 * intervals. It runs in ":PlayerProcess" and can therefore use [LocalBroadcastManager] to
 * share the infos with [PlayerService].
 */
class ShowInfoFetcher
(private val context: Context) : Runnable {
    private val LOGGER = LoggerFactory.getLogger(ShowInfoFetcher::class.java)
    private val api: MetalOnlyAPI_ = MetalOnlyAPI_(context)
    private var active: Boolean = false

    private val err: Boolean = false

    /**
     * stops the meta data receiver
     */
    fun stop() {
        active = false
    }

    /**
     * gets meta data every 15 seconds
     */
    override fun run() {
        active = true
        while (active && !err) {
            try {
                val stats = api.stats

                if (stats != null) {
                        broadcastCurrentShowInfo(stats)
                }
            } catch (e: RestClientException) {
                /** FIXME handle this  */
                LOGGER.error(e.message, e)
            } catch (e: NoInternetException) {
                /** FIXME handle this  */
                LOGGER.error(e.message, e)
                sleepFetcherFor(NO_INTERNET_SLEEP_INTERVAL)
            }

            sleepFetcherFor(UPDATE_INTERVAL)
        }
    }

    private fun broadcastCurrentShowInfo(stats: Stats) {
        val track = stats.track

        val intent = Intent(ShowInfoIntentConstants.INTENT_NEW_TRACK)
        intent.putExtra(ShowInfoIntentConstants.KEY_ARTIST, track.artist)
        intent.putExtra(ShowInfoIntentConstants.KEY_TITLE, track.title)
        intent.putExtra(ShowInfoIntentConstants.KEY_MODERATOR, stats.moderator)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    private fun sleepFetcherFor(interval: Int) {
        try {
            Thread.sleep(interval.toLong())
        } catch (e: InterruptedException) {
            // everything is fine
        }

    }

    companion object {

        private val _15_MIN_IN_MILLIS = 15 * 1000
        private val UPDATE_INTERVAL = if (BuildConfig.DEBUG) 5000 else _15_MIN_IN_MILLIS
        private val NO_INTERNET_SLEEP_INTERVAL = 30 * 1000
    }
}

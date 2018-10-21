package com.codingspezis.android.metalonly.player.stream.trackinfo

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.codingspezis.android.metalonly.player.BuildConfig
import com.github.ironjan.metalonly.client.MetalOnlyClientV2
import com.github.ironjan.metalonly.client.NoInternetException
import com.github.ironjan.metalonly.client.model.StatsV2
import org.slf4j.LoggerFactory
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClientException

/**
 * A [Runnable] that will fetch the current show information (moderator and track) in regular
 * intervals. It runs in ":PlayerProcess" and can therefore use [LocalBroadcastManager] to
 * share the infos with [PlayerService].
 *
 */
class ShowInfoFetcher
(private val context: Context) : Runnable {
    private val LOGGER = LoggerFactory.getLogger(ShowInfoFetcher::class.java)
    private val client: MetalOnlyClientV2 = MetalOnlyClientV2.getClient(context)
    private var active: Boolean = false

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
        while (active) {
            try {
                client.getStats().map { it -> broadcastCurrentShowInfo(it) }
            } catch (e: RestClientException) {
                /* Currently, all exceptions are evil. We need to find a better method to handle
                 * these - currently, there is nothing to do and we just stop execution to not waste
                 * any battery or data. */
                LOGGER.error(e.message, e)
                active = false
            } catch (e: NoInternetException) {
                /* We will sleep for some time. If internet fails, [stop] will be called by the
                 * stream  managing class*/
                sleepFetcherFor(NO_INTERNET_SLEEP_INTERVAL)
            } catch (e: ResourceAccessException) {
                /* We will sleep for some time. If internet fails, [stop] will be called by the
                 * stream  managing class*/
                sleepFetcherFor(NO_INTERNET_SLEEP_INTERVAL)
            }

            sleepFetcherFor(UPDATE_INTERVAL)
        }
    }

    private fun broadcastCurrentShowInfo(stats: StatsV2) {
        val track = stats.track
        val artist = track.artist
        val title = track.title
        val moderator = stats.showInformation.moderator

        val intent = Intent(ShowInfoIntentConstants.INTENT_NEW_TRACK)
        intent.putExtra(ShowInfoIntentConstants.KEY_ARTIST, artist)
        intent.putExtra(ShowInfoIntentConstants.KEY_TITLE, title)
        intent.putExtra(ShowInfoIntentConstants.KEY_MODERATOR, moderator)
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

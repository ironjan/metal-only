package com.codingspezis.android.metalonly.player.stream.track_info

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

import com.codingspezis.android.metalonly.player.BuildConfig
import com.codingspezis.android.metalonly.player.core.Track
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPIWrapper
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPIWrapper_
import com.codingspezis.android.metalonly.player.utils.jsonapi.NoInternetException

import org.slf4j.LoggerFactory
import org.springframework.web.client.RestClientException

class TrackInfoFetcher
/**
 * constructor
 */
(private val context: Context) : Runnable {
    private val LOGGER = LoggerFactory.getLogger(TrackInfoFetcher::class.java)
    private val apiWrapper: MetalOnlyAPIWrapper
    private var active: Boolean = false
    private val err: Boolean = false

    init {
        apiWrapper = MetalOnlyAPIWrapper_.getInstance_(context)
    }

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
                val trackWrapper = apiWrapper.track

                if (trackWrapper != null) {
                    val track = trackWrapper.track
                    if (track != null) {
                        broadcastTrackInfo(track)
                    }
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

    private fun broadcastTrackInfo(track: Track) {
        val intent = Intent(TrackInfoIntentConstants.INTENT_NEW_TRACK)
        intent.putExtra(TrackInfoIntentConstants.KEY_ARTIST, track.artist)
        intent.putExtra(TrackInfoIntentConstants.KEY_TITLE, track.title)
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

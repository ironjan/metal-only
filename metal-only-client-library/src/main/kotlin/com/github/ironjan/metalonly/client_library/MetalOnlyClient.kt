package com.github.ironjan.metalonly.client_library

import android.content.Context
import org.springframework.web.client.RestClientException

public interface MetalOnlyClient {
    /**
     * Requests the show's stats
     *
     * @return the show's stats. May be null when errors occur.
     */
    fun getStats(): Stats?

    /**
     * Requests this week's sending plan
     *
     * @return this week's sending plan. May be null when errors occur.
     */
    @Throws(RestClientException::class, NoInternetException::class)
    fun getPlan(): Plan?

    /**
     * Gets the current track via API
     *
     * @return the currently played track. May be null when errors occur.
     *
     * @throws RestClientException when a REST related exception occurs
     * @throws NoInternetException when this method is called without internet connection
     */
    @Throws(RestClientException::class, NoInternetException::class)
    fun getTrack(): TrackWrapper?

    companion object {

        fun getClient(context: Context): MetalOnlyClient {
            return MetalOnlyAPIWrapper_.getInstance_(context);
        }
    }
}
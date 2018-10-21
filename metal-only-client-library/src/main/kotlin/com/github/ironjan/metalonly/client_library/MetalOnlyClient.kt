package com.github.ironjan.metalonly.client_library

import android.content.Context
import org.springframework.web.client.RestClientException

/**
 * Interface to be used for calling the Metal Only Client.
 */
interface MetalOnlyClient {

    /**
     * Requests this week's sending plan
     *
     * @return this week's sending plan. May be null when errors occur.
     *
     * @throws NoInternetException if no internet connection is present
     *
     * @throws RestClientException rethrow of underlying API implementation exception
     *
     * @throws ResourceAccessException if there are network problems
     */
    @Throws(RestClientException::class, NoInternetException::class)
    fun getPlan(): Plan?

    companion object {

        /**
         * Gets a singleton instance of the client for usage.
         */
        fun getClient(context: Context): MetalOnlyClient {
            return MetalOnlyClientImplementation_.getInstance_(context)
        }
    }
}
package com.github.ironjan.metalonly.client_library

import android.content.Context
import arrow.core.Either
import com.github.ironjan.metalonly.client_library.model.Track

interface MetalOnlyClientV2 {
    fun getTrack(): Either<String, Track>

    companion object {

        /**
         * Gets a singleton instance of the client for usage.
         */
        fun getClient(context: Context): MetalOnlyClientV2 {
            return MetalOnlyClientV2Implementation_.getInstance_(context)
        }
    }
}
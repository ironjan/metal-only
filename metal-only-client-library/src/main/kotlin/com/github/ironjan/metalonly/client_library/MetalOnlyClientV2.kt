package com.github.ironjan.metalonly.client_library

import android.content.Context
import arrow.core.Either
import com.github.ironjan.metalonly.client_library.model.PlanEntry
import com.github.ironjan.metalonly.client_library.model.StatsV2
import com.github.ironjan.metalonly.client_library.model.ShowInformation
import com.github.ironjan.metalonly.client_library.model.Track

interface MetalOnlyClientV2 {
    fun getTrack(): Either<String, Track>

    fun getShowInfomation(): Either<String, ShowInformation>

    fun getStats(): Either<String, StatsV2>

    fun getPlan(): Either<String, Array<PlanEntry>>

    companion object {

        /**
         * Gets a singleton instance of the client for usage.
         */
        fun getClient(context: Context): MetalOnlyClientV2 {
            return MetalOnlyClientV2Implementation_.getInstance_(context)
        }
    }
}
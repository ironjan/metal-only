package com.github.ironjan.metalonly.client

import android.content.Context
import arrow.core.Either
import com.github.ironjan.metalonly.client.model.PlanEntry
import com.github.ironjan.metalonly.client.model.StatsV2
import com.github.ironjan.metalonly.client.model.ShowInformation
import com.github.ironjan.metalonly.client.model.Track
import com.github.ironjan.metalonly.client.model.Wish

interface MetalOnlyClientV2 {
    fun getTrack(): Either<String, Track>

    fun getShowInfomation(): Either<String, ShowInformation>

    fun getStats(): Either<String, StatsV2>

    fun getPlan(): Either<String, Array<PlanEntry>>

    fun sendWish(wish: Wish): Either<String, Boolean>

    companion object {

        /**
         * Gets a singleton instance of the client for usage.
         */
        fun getClient(context: Context): MetalOnlyClientV2 {
            return MetalOnlyClientV2Implementation_.getInstance_(context)
        }
    }
}
package com.codingspezis.android.metalonly.player.stream.metadata

import com.codingspezis.android.metalonly.player.core.HistoricTrack

import java.util.Calendar

/**
 * Represents show meta data
 */
data class Metadata internal constructor(val moderator: String, val genre: String, private val interpret: String, val title: String) {

    fun toSong(): HistoricTrack {
        val date = Calendar.getInstance().timeInMillis

        val song = HistoricTrack(interpret, title, moderator, date)

        return song
    }

}
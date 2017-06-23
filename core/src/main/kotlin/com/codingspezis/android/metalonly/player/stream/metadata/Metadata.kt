package com.codingspezis.android.metalonly.player.stream.metadata

import com.codingspezis.android.metalonly.player.core.Song

import java.util.Calendar

/**
 * Represents show meta data
 */
data class Metadata internal constructor(val moderator: String, val genre: String, private val interpret: String, val title: String) {

    fun toSong(): Song {
        val date = Calendar.getInstance().timeInMillis

        val song = Song(interpret, title, moderator, date)

        return song
    }

}
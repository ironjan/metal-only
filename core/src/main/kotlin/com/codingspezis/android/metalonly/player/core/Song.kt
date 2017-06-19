package com.codingspezis.android.metalonly.player.core

import java.util.Date

/**
 * Represents a song with its properties. <code>thumb</code> is a cleaned variant of <code>thumbRaw</code>,
 * i.e. <code>thumb</code> without <code>" OnAir"</code>.
 *
 * TODO Clean up the overrides and remove dupolicate fields
 */
data class Song(val interpret: String, override val title: String, val thumbRaw: String, val date: Long)
    : Track, HistoricSongExtension {
    val thumb: String = thumbRaw.replace(" OnAir", "")
    fun withClearedThumb(): Song {
        return Song(interpret, title, "", date)
    }
    val isValid: Boolean
        get() = interpret.isNotEmpty() && title.isNotEmpty()

    override val moderator: String = thumb

    override val artist: String = interpret

    override val datePlayed: Date = Date(date)
}

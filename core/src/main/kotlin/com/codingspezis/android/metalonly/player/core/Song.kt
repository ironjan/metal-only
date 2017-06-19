package com.codingspezis.android.metalonly.player.core

/**
 * Represents a song with its properties. <code>thumb</code> is a cleaned variant of <code>thumbRaw</code>,
 * i.e. <code>thumb</code> without <code>" OnAir"</code>.
 */
data class Song(override val artist: String, override val title: String, private val thumbRaw: String, override val playedAtAsLong: Long)
    : Track, HistoricSongExtension {

    override val moderator: String = thumbRaw.replace(" OnAir", "")

    val isValid: Boolean
        get() = artist.isNotEmpty() && title.isNotEmpty()

    fun withClearedThumb(): Song {
        return Song(artist, title, "", playedAtAsLong)
    }
}

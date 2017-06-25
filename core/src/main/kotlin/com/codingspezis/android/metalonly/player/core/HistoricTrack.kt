package com.codingspezis.android.metalonly.player.core

data class HistoricTrack(override val artist: String,
                         override val title: String,
                         private val thumbRaw: String,
                         override val playedAtAsLong: Long)
    : Track, HistoricTrackExtension {

    override val moderator: String = thumbRaw.replace(" OnAir", "")

    val isValid: Boolean
        get() = artist.isNotEmpty() && title.isNotEmpty()

    fun withClearedThumb(): HistoricTrack {
        return HistoricTrack(artist, title, "", playedAtAsLong)
    }

    /**
     * Compares everything except [playedAtAsLong].
     */
    fun limitedEquals(other: HistoricTrack?): Boolean {
        if (other == null) return false
        else
            return artist == other.artist
                    && moderator == other.moderator
                    && title == other.title
    }
}

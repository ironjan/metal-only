package com.codingspezis.android.metalonly.player.core

import java.util.Comparator

data class HistoricTrack(override val artist: String,
                         override val title: String,
                         val thumbRaw: String,
                         override val playedAtAsLong: Long)
    : Track, HistoricTrackExtension, Comparable<HistoricTrack> {

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

    override fun compareTo(other: HistoricTrack): Int {
        return this.playedAtAsLong.compareTo(other.playedAtAsLong)
    }

    class HistoricTrack_SortedByPlayedAs_Desc_Comparator : Comparator<HistoricTrack> {
        override fun compare(o1: HistoricTrack, o2: HistoricTrack): Int {
            return -o1.compareTo(o2)
        }

    }

}

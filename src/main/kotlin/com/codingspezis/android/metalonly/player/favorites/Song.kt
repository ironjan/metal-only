package com.codingspezis.android.metalonly.player.favorites

import java.util.Calendar

/**
 * class for holding a song
 */
class Song {
    val interpret: String
    val title: String
    val date: Long

    val thumb: String

    /**
     * Creates a Song with the given parameters
     * @param interpret song artist
     * @param title song title
     * @param thumb name of moderator's picture
     * @param date when this song was played
     */
    constructor(interpret: String, title: String, thumb: String, date: Long) : super() {
        this.interpret = interpret
        this.title = title
        this.thumb = thumb.replace(" OnAir", "")
        this.date = date
    }

    /**
     * Wrapper for [.Song] with date set to the moment this constructor is called
     * @param interpret song artist
     * @param title song title
     * @param thumb name of moderator's picture
     *
     * @deprecated Use the full constructor instead
     */
    @JvmOverloads constructor(interpret: String, title: String, thumb: String = "") : super() {
        this.interpret = interpret
        this.title = title
        this.thumb = thumb.replace(" OnAir", "")
        this.date = Calendar.getInstance().timeInMillis
    }

    fun withClearedThumb(): Song {
        return Song(interpret, title, "", date)
    }

    /**
     * @return true, if this is a valid song. False if invalid
     */
    val isValid: Boolean
        get() = interpret.isNotEmpty() && title.isNotEmpty()
}

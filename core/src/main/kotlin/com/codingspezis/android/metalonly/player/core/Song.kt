package com.codingspezis.android.metalonly.player.core

/**
 * class for holding a song
 */
class Song(val interpret: String, val title: String, thumb: String, val date: Long) {
    val thumb: String = thumb.replace(" OnAir", "")

    fun withClearedThumb(): Song {
        return Song(interpret, title, "", date)
    }

    /**
     * @return true, if this is a valid song. False if invalid
     */
    val isValid: Boolean
        get() = interpret.isNotEmpty() && title.isNotEmpty()

}

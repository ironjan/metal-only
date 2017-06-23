package com.codingspezis.android.metalonly.player.core

/**
 * Represents a song with its properties. <code>thumb</code> is a cleaned variant of <code>thumbRaw</code>,
 * i.e. <code>thumb</code> without <code>" OnAir"</code>.
 */
data class Song(val interpret: String, val title: String, val thumbRaw: String, val date: Long) {
    val thumb: String = thumbRaw.replace(" OnAir", "")

    fun withClearedThumb(): Song {
        return Song(interpret, title, "", date)
    }

    val isValid: Boolean
        get() = interpret.isNotEmpty() && title.isNotEmpty()
}

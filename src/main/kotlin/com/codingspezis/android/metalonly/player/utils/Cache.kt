package com.codingspezis.android.metalonly.player.utils

import android.graphics.Bitmap

/**
 * Interface for cache implementations
 */
interface Cache {
    operator fun get(moderator: String?): Bitmap?
    operator fun set(moderator: String, newBitmap: Bitmap?)
    fun clear()
}
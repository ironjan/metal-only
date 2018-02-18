package com.codingspezis.android.metalonly.player.utils

import com.hypertrack.hyperlog.HyperLog

import java.io.InputStream
import java.io.OutputStream

/**
 * Implementation to copy from one input stream to an output stream. Based on the corresponding
 * class from [https://github.com/thest1/LazyList](https://github.com/thest1/LazyList).
 */
object CopyStreamImplementation {
    fun copy(`is`: InputStream, os: OutputStream) {
        val buffer_size = 1024
        try {
            val bytes = ByteArray(buffer_size)
            var count: Int = 0
            while (count != -1) {
                count = `is`.read(bytes, 0, buffer_size)
                os.write(bytes, 0, count)
            }
        } catch (ex: Exception) {
            HyperLog.e(CopyStreamImplementation::class.java.simpleName, ex.message, ex)
        }

    }
}
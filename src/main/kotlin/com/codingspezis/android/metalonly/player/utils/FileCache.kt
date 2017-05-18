package com.codingspezis.android.metalonly.player.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import com.codingspezis.android.metalonly.player.R
import com.codingspezis.android.metalonly.player.StreamControlActivity

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar

/**
 * this class comes in the original form from:
 * https://github.com/thest1/LazyList
 *
 *
 * we modified it so that it works with internal storage and a cache duration of
 * 7 days it works not longer with URLs but with getModerator names of metal only
 * some synchronized statements are also added
 */
class FileCache(private val context: Context) {

    @Synchronized @Throws(FileNotFoundException::class)
    fun getOutputStream(moderator: String): FileOutputStream {
        val fileName = moderator.hashCode().toString()
        if (hasThumb(context, fileName) != null) {
            context.deleteFile(fileName)
        }

        val editor = context.getSharedPreferences(context.getString(R.string.app_name), 0).edit()
        editor.putLong(StreamControlActivity.KEY_SP_MODTHUMBDATE + fileName, Calendar
                .getInstance().timeInMillis)
        editor.apply()

        return context.openFileOutput(fileName, Context.MODE_PRIVATE)
    }

    /**
     * decodes image and scales it to reduce memory consumption
     * @param moderator
     * *
     * @return
     */
    @Synchronized fun decodeMod(moderator: String): Bitmap? {
        val fileName = hasThumb(context, moderator)
        if (fileName != null) {
            try {
                // decode image size
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                val stream1 = context.openFileInput(fileName)

                BitmapFactory.decodeStream(stream1, null, o)
                stream1.close()

                // Find the correct scale value. It should be the power of 2.
                val REQUIRED_SIZE = 70
                var width_tmp = o.outWidth
                var height_tmp = o.outHeight
                var scale = 1
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                        break
                    }

                    width_tmp /= 2
                    height_tmp /= 2
                    scale *= 2
                }

                // decode with inSampleSize
                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                val stream2 = context.openFileInput(fileName)

                val bitmap = BitmapFactory.decodeStream(stream2, null, o2)
                stream2.close()

                return bitmap
            } catch (e: IOException) {
                Log.e(TAG, e.message, e)
            }

        }
        return null
    }

    fun clear() {
        clear(context)
    }

    companion object {

        // getModerator thumbs are cached 7 days in internal file storage
        val WEEK_IN_MILLISECS = (7 * 24 * 60 * 60 * 1000).toLong()
        private val TAG = FileCache::class.java.simpleName

        /**
         * checks if thumb of getModerator is already loaded
         * @param context   context of private storage
         * *
         * @param moderator The getModerator's name
         * *
         * @return file name of thumb if exists - null otherwise
         */
        fun hasThumb(context: Context, moderator: String): String? {
            val fileName = moderator.hashCode().toString()
            val files = context.fileList()

            for (file in files) {
                if (file == fileName) {
                    return file
                }
            }
            return null
        }

        /**
         * checks cache duration of special file

         * @param context  context for shared preferences
         * *
         * @param fileName name of file
         * *
         * @return true if file is older than cache duration
         */
        fun isTooOld(context: Context, fileName: String): Boolean {
            val settings = context.getSharedPreferences(context.getString(R.string.app_name), 0)
            val modThumDate = settings.getLong(StreamControlActivity.KEY_SP_MODTHUMBDATE + fileName, 0)
            val currentDate = Calendar.getInstance().timeInMillis

            return currentDate - modThumDate > WEEK_IN_MILLISECS
        }

        @Synchronized fun clear(context: Context) {
            val files = context.fileList()
            for (file in files) {
                if (isTooOld(context, file)) {
                    context.deleteFile(file)
                }
            }
        }
    }

}
package com.codingspezis.android.metalonly.player.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.widget.ImageView

import com.codingspezis.android.metalonly.player.R

import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * this class comes in the original form from:
 * https://github.com/thest1/LazyList
 *
 *
 * it works not longer with URLs but with getModerator names of metal only some
 * synchronized statements are also added
 */
class ImageLoader(context: Context) {

    internal val stub_id = R.drawable.mo_wait
    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView, String>())

    private val memoryCache = MemoryCache()
    private val fileCache: FileCache
    private val executorService: ExecutorService
    private val handler = Handler()

    init {
        fileCache = FileCache(context)
        executorService = Executors.newFixedThreadPool(5)
    }

    fun displayImage(moderator: String, imageView: ImageView) {
        imageViews.put(imageView, moderator)

        val bitmap = memoryCache[moderator]

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            queuePhoto(moderator, imageView)
            imageView.setImageResource(stub_id)
        }
    }

    private fun queuePhoto(moderator: String, imageView: ImageView) {
        val p = ModImageLoadingQueueItem(moderator, imageView)
        executorService.submit(PhotosLoader(p))
    }

    @Synchronized internal fun getBitmap(moderator: String): Bitmap? {
        val b = fileCache.decodeMod(moderator)
        if (b != null) {
            return b
        }

        try {
            val imageUrl = URL(UrlConstants.METAL_ONLY_MODERATOR_PIC_BASE_URL + moderator)

            val conn = imageUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 30000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = true

            val `is` = conn.inputStream
            val os = fileCache.getOutputStream(moderator)

            CopyStreamImplementation.copy(`is`, os)

            `is`.close()
            os.close()

            return fileCache.decodeMod(moderator)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            if (ex is OutOfMemoryError) {
                memoryCache.clear()
            }
            return null
        }

    }

    internal fun imageViewReused(modImageLoadingQueueItem: ModImageLoadingQueueItem): Boolean {
        val tag = imageViews[modImageLoadingQueueItem.imageView]
        return tag == null || tag != modImageLoadingQueueItem.moderator
    }

    internal inner class PhotosLoader(var modImageLoadingQueueItem: ModImageLoadingQueueItem) : Runnable {

        override fun run() {
            try {
                if (imageViewReused(modImageLoadingQueueItem)) {
                    return
                }
                val bmp = getBitmap(modImageLoadingQueueItem.moderator)
                memoryCache.put(modImageLoadingQueueItem.moderator, bmp!!)
                if (imageViewReused(modImageLoadingQueueItem)) {
                    return
                }
                val bd = BitmapDisplayer(this@ImageLoader, bmp,
                        modImageLoadingQueueItem)
                handler.post(bd)
            } catch (th: Throwable) {
                th.printStackTrace()
            }

        }
    }

}

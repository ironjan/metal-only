package com.codingspezis.android.metalonly.player.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.widget.ImageView
import org.androidannotations.api.UiThreadExecutor
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

    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView, String>())

    private val memoryCache: Cache = MemoryCache()
    private val fileCache: Cache = FileCache(context)

    /** @todo use actual number of cpu cores here */
    private val executorService: ExecutorService = Executors.newFixedThreadPool(8)
    private val handler = Handler()

    fun loadImage(moderator: String, imageView: ImageView) {
        imageViews.put(imageView, moderator)

        val memCachedBm = memoryCache[moderator]
        if (memCachedBm != null) {
            replaceImage(imageView, memCachedBm)
            return
        }

        val fileCachedBm = fileCache[moderator]
        if (fileCachedBm != null) {
            memoryCache[moderator] = fileCachedBm
            replaceImage(imageView, fileCachedBm)
            return
        }

        executorService.submit(ImageDownloader(ModImageLoadingQueueItem(moderator, imageView)))
    }

    internal fun replaceImage(imageView: ImageView, bitmap: Bitmap) {
        UiThreadExecutor.runTask("", { imageView.setImageBitmap(bitmap) }, 0L)
    }

    internal fun imageViewReused(modImageLoadingQueueItem: ModImageLoadingQueueItem): Boolean {
        val tag = imageViews[modImageLoadingQueueItem.imageView]
        return tag == null || tag != modImageLoadingQueueItem.moderator
    }

    internal inner class ImageDownloader(var queueItem: ModImageLoadingQueueItem) : Runnable {

        override fun run() {
            try {
                if (imageViewReused(queueItem)) {
                    return
                }

                val bitmap = downloadImage(queueItem.moderator)

                memoryCache[queueItem.moderator] = bitmap

                if (imageViewReused(queueItem)) {
                    return
                }
                val bd = BitmapDisplayer(this@ImageLoader, bitmap, queueItem)
                handler.post(bd)
            } catch (th: Throwable) {
                th.printStackTrace()
            }
        }

        @Synchronized internal fun downloadImage(moderator: String): Bitmap? {
            try {
                val imageUrl = URL(UrlConstants.METAL_ONLY_MODERATOR_PIC_BASE_URL + moderator)

                val conn = imageUrl.openConnection() as HttpURLConnection
                conn.connectTimeout = 30000
                conn.readTimeout = 30000
                conn.instanceFollowRedirects = true

                val inputStream = conn.inputStream

                val bitmap = BitmapFactory.decodeStream(inputStream, null, BitmapFactory.Options())
                fileCache[moderator] = bitmap
                inputStream.close()

                return fileCache[moderator]
            } catch (ex: Throwable) {
                ex.printStackTrace()
                if (ex is OutOfMemoryError) {
                    memoryCache.clear()
                }
                return null
            }
        }
    }

}

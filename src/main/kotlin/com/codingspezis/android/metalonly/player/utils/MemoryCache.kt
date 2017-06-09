package com.codingspezis.android.metalonly.player.utils

import android.graphics.Bitmap
import android.util.Log
import java.util.Collections

/**
 * A memory cache based on  [https://github.com/thest1/LazyList](https://github.com/thest1/LazyList
 * which uses 25% of the available heap to allocate images.
 */
internal class MemoryCache : Cache {
    private var allocatedBytes: Long = 0
    private val maxMemoryInBytes: Long = Runtime.getRuntime().maxMemory() / 4


    override fun set(moderator: String, newBitmap: Bitmap?) {
        try {
            val oldBitmap: Bitmap? = if (MEMORY_CACHE.containsKey(moderator)) MEMORY_CACHE[moderator]
            else null

            deallocate(oldBitmap)
            MEMORY_CACHE.put(moderator, newBitmap!!)
            allocate(newBitmap)

            reduceAllocationSpaceToMaximum()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override operator fun get(moderator: String?): Bitmap? {
        try {
            return if (MEMORY_CACHE.containsKey(moderator)) MEMORY_CACHE[moderator]
            else null
        } catch (ex: NullPointerException) {
            /* NPE may happen here  http://code.google.com/p/osmdroid/issues/detail?id=78 so we ignore */
            return null
        }
    }

    private fun reduceAllocationSpaceToMaximum() {
        Log.i(TAG, "MEMORY_CACHE allocatedBytes=$allocatedBytes length=${MEMORY_CACHE.size}")

        if (allocatedBytes > maxMemoryInBytes) {
            // least recently accessed item will be the first one iterated
            val iterator = MEMORY_CACHE.entries.iterator()

            while (iterator.hasNext() && allocatedBytes <= maxMemoryInBytes) {
                val entry = iterator.next()
                deallocate(entry.value)
                iterator.remove()
            }
            Log.i(TAG, "Clean MEMORY_CACHE. New allocatedBytes ${MEMORY_CACHE.size}")
        }
    }

    private fun allocate(newBitmap: Bitmap) {
        allocatedBytes += getSizeInBytes(newBitmap)
    }

    private fun deallocate(bitmap: Bitmap?) {
        allocatedBytes -= getSizeInBytes(bitmap)
    }

    private fun getSizeInBytes(bitmap: Bitmap?): Long {
        return if (bitmap != null) (bitmap.rowBytes * bitmap.height).toLong()
        else 0
    }

    override fun clear() {
        try {
            MEMORY_CACHE.clear()
            allocatedBytes = 0
        } catch (ex: NullPointerException) {
            /* NPE may happen here  http://code.google.com/p/osmdroid/issues/detail?id=78 so we ignore */
        }
    }

    companion object {
        private val TAG = MemoryCache::class.java.simpleName
        private val LRU_ACCESS_MAP = LinkedHashMap<String, Bitmap>(10, 1.5f, true)
        private val MEMORY_CACHE = Collections.synchronizedMap(LRU_ACCESS_MAP)
    }
}
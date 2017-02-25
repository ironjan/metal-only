package com.codingspezis.android.metalonly.player.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * this class comes in the original form from:
 * https://github.com/thest1/LazyList
 */
public class MemoryCache {

    private static final String TAG = MemoryCache.class.getSimpleName();

    // Last argument true for LRU ordering
    private final Map<String, Bitmap> cache = Collections
            .synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));

    private long allocatedSize = 0;// current allocated allocatedSize
    private long maxMemoryInBytes = 1000000;// max memory in bytes

    public MemoryCache() {
        // use 25% of available heap allocatedSize
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    static long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public void setLimit(long new_limit) {
        maxMemoryInBytes = new_limit;
        Log.i(TAG, "MemoryCache will use up to " + maxMemoryInBytes / 1024.
                / 1024. + "MB");
    }

    public Bitmap get(String id) {
        try {
            if (!cache.containsKey(id)) {
                return null;
            }
            // NullPointerException sometimes happen here
            // http://code.google.com/p/osmdroid/issues/detail?id=78
            return cache.get(id);
        } catch (NullPointerException ex) {
            Log.e(TAG, ex.getMessage(), ex);
            return null;
        }
    }

    public void put(String id, Bitmap bitmap) {
        try {
            if (cache.containsKey(id)) {
                allocatedSize -= getSizeInBytes(cache.get(id));
            }
            cache.put(id, bitmap);
            allocatedSize += getSizeInBytes(bitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void checkSize() {
        Log.i(TAG,
                "cache allocatedSize=" + allocatedSize + " length="
                        + cache.size()
        );
        if (allocatedSize > maxMemoryInBytes) {
            // least recently accessed item will be the first one iterated
            Iterator<Entry<String, Bitmap>> iterator = cache.entrySet()
                    .iterator();

            while (iterator.hasNext()) {
                Entry<String, Bitmap> entry = iterator.next();
                allocatedSize -= getSizeInBytes(entry.getValue());
                iterator.remove();
                if (allocatedSize <= maxMemoryInBytes) {
                    break;
                }
            }
            Log.i(TAG, "Clean cache. New allocatedSize " + cache.size());
        }
    }

    public void clear() {
        try {
            // NullPointerException sometimes happen here
            // http://code.google.com/p/osmdroid/issues/detail?id=78
            cache.clear();
            allocatedSize = 0;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
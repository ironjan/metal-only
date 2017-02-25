package com.codingspezis.android.metalonly.player.utils;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * this class comes in the original form from:
 * https://github.com/thest1/LazyList
 */
public class Utils {
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            Log.e(Utils.class.getSimpleName(), ex.getMessage(), ex);
        }
    }
}
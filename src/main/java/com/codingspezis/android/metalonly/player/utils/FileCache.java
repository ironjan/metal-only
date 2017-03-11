package com.codingspezis.android.metalonly.player.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.StreamControlActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * this class comes in the original form from:
 * https://github.com/thest1/LazyList
 * <p/>
 * we modified it so that it works with internal storage and a cache duration of
 * 7 days it works not longer with URLs but with moderator names of metal only
 * some synchronized statements are also added
 */
public class FileCache {

    // moderator thumbs are cached 7 days in internal file storage
    public static final long WEEK_IN_MILLISECS = 7 * 24 * 60 * 60 * 1000;
    private static final String TAG = FileCache.class.getSimpleName();
    private final Context context;

    public FileCache(Context context) {
        this.context = context;
    }

    /**
     * checks if thumb of moderator is already loaded
     *
     * @param context   context of private storage
     * @param moderator The moderator's name
     * @return file name of thumb if exists - null otherwise
     */
    public static String hasThumb(Context context, String moderator) {
        String fileName = String.valueOf(moderator.hashCode());
        String files[] = context.fileList();

        for (int i = 0; i < files.length; i++) {
            if (files[i].equals(fileName)) {
                return files[i];
            }
        }
        return null;
    }

    /**
     * checks cache duration of special file
     *
     * @param context  context for shared preferences
     * @param fileName name of file
     * @return true if file is older than cache duration
     */
    public static boolean isTooOld(Context context, String fileName) {
        SharedPreferences settings = context.getSharedPreferences(
                context.getString(R.string.app_name), 0);
        long modThumDate = settings.getLong(StreamControlActivity.KEY_SP_MODTHUMBDATE
                + fileName, 0);
        long currentDate = Calendar.getInstance().getTimeInMillis();

        return (currentDate - modThumDate) > WEEK_IN_MILLISECS;
    }

    public static synchronized void clear(Context context) {
        String files[] = context.fileList();
        for (String file : files) {
            if (isTooOld(context, file)) {
                context.deleteFile(file);
            }
        }
    }

    public synchronized FileOutputStream getOutputStream(String moderator)
            throws FileNotFoundException {
        String fileName = String.valueOf(moderator.hashCode());
        if (hasThumb(context, fileName) != null) {
            context.deleteFile(fileName);
        }

        Editor editor = context.getSharedPreferences(
                context.getString(R.string.app_name), 0).edit();
        editor.putLong(StreamControlActivity.KEY_SP_MODTHUMBDATE + fileName, Calendar
                .getInstance().getTimeInMillis());
        editor.commit();

        return context.openFileOutput(fileName, Context.MODE_PRIVATE);
    }

    /**
     * decodes image and scales it to reduce memory consumption
     *
     * @param moderator
     * @return
     */
    public synchronized Bitmap decodeMod(String moderator) {
        String fileName = hasThumb(context, moderator);
        if (fileName != null) {
            try {
                // decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                FileInputStream stream1 = context.openFileInput(fileName); // new
                // FileInputStream(f);
                BitmapFactory.decodeStream(stream1, null, o);
                stream1.close();

                // Find the correct scale value. It should be the power of 2.
                final int REQUIRED_SIZE = 70;
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                int scale = 1;
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE
                            || height_tmp / 2 < REQUIRED_SIZE) {
                        break;
                    }

                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }

                // decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                FileInputStream stream2 = context.openFileInput(fileName);

                Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
                stream2.close();

                return bitmap;
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return null;
    }

    public void clear() {
        clear(context);
    }

}
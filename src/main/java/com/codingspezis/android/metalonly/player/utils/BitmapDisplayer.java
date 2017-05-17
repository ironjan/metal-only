package com.codingspezis.android.metalonly.player.utils;

import android.graphics.Bitmap;

//Used to display bitmap in the UI thread
class BitmapDisplayer implements Runnable {
    private final ImageLoader imageLoader;

    Bitmap bitmap;

    ModImageLoadingQueueItem modImageLoadingQueueItem;

    public BitmapDisplayer(ImageLoader imageLoader, Bitmap b, ModImageLoadingQueueItem p) {
        this.imageLoader = imageLoader;
        bitmap = b;
        modImageLoadingQueueItem = p;
    }

    @Override
    public void run() {
        if (this.imageLoader.imageViewReused(modImageLoadingQueueItem)) {
            return;
        }

        if (bitmap != null) {
            modImageLoadingQueueItem.imageView.setImageBitmap(bitmap);
        } else {
            modImageLoadingQueueItem.imageView.setImageResource(this.imageLoader.stub_id);
        }
    }
}
package com.codingspezis.android.metalonly.player.utils

import android.graphics.Bitmap

/**
 * Class to bind a Bitmap to an ImageView on the UIThread
 */
internal class BitmapDisplayer(private val imageLoader: ImageLoader, var bitmap: Bitmap?, var modImageLoadingQueueItem: ModImageLoadingQueueItem) : Runnable {

    override fun run() {
        if (this.imageLoader.imageViewReused(modImageLoadingQueueItem)) {
            return
        }

        if (bitmap != null) {
            modImageLoadingQueueItem.imageView.setImageBitmap(bitmap)
        } else {
            modImageLoadingQueueItem.imageView.setImageResource(this.imageLoader.stub_id)
        }
    }
}
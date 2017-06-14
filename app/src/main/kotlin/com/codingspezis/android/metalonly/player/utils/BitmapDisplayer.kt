package com.codingspezis.android.metalonly.player.utils

import android.graphics.Bitmap
import com.codingspezis.android.metalonly.player.R

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
            modImageLoadingQueueItem.imageView.setImageResource(R.drawable.mo_wait)
        }
    }
}
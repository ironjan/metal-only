package com.codingspezis.android.metalonly.player.utils;

import android.widget.ImageView;

/**
 * Represents one mod image in the loading queue.
 */
class ModImageLoadingQueueItem {
    final String moderator;
    final ImageView imageView;

    ModImageLoadingQueueItem(String moderator, ImageView imageView) {
        this.moderator = moderator;
        this.imageView = imageView;
    }
}

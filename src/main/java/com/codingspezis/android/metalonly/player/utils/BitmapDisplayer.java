package com.codingspezis.android.metalonly.player.utils;

import android.graphics.*;

import com.codingspezis.android.metalonly.player.utils.ImageLoader.PhotoToLoad;

//Used to display bitmap in the UI thread
class BitmapDisplayer implements Runnable {
	private final ImageLoader imageLoader;

	Bitmap bitmap;

	PhotoToLoad photoToLoad;

	public BitmapDisplayer(ImageLoader imageLoader, Bitmap b, PhotoToLoad p) {
		this.imageLoader = imageLoader;
		bitmap = b;
		photoToLoad = p;
	}

	@Override
	public void run() {
		if (this.imageLoader.imageViewReused(photoToLoad)) {
			return;
		}

		if (bitmap != null) {
			photoToLoad.imageView.setImageBitmap(bitmap);
		} else {
			photoToLoad.imageView.setImageResource(this.imageLoader.stub_id);
		}
	}
}
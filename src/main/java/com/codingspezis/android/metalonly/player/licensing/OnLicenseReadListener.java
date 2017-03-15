package com.codingspezis.android.metalonly.player.licensing;

/**
 * listener for ThreadedLicenseReader
 */
public interface OnLicenseReadListener {
    /**
     * This method is called when a license has been read from the file system
     */
    void onLicenseRead();
}
package com.codingspezis.android.metalonly.player.licensing;

import com.codingspezis.android.metalonly.player.LicenseActivity;
import com.codingspezis.android.metalonly.player.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * loads licenses asynchronous
 */
public class ThreadedLicenseReader extends Thread {

    /**
     *
     */
    private final LicenseActivity licenseActivity;

    private OnLicenseReadListener onLicenseReadListener;

    private String license;

    public ThreadedLicenseReader(LicenseActivity licenseActivity, String license) {
        this.licenseActivity = licenseActivity;
        this.license = license;
    }

    @Override
    public void run() {
        try {
            license = getLicenseText(license);
        } catch (Exception e) {
            license = null;
        }
        if (onLicenseReadListener != null) {
            onLicenseReadListener.onLicenseRead();
        }
    }

    /**
     * getter for license
     *
     * @return license as string
     */
    public String getLicense() {
        return license;
    }

    /**
     * setter for listener
     *
     * @param onLicenseReadListener listener
     */
    public void setOnLicenseReadListener(
            OnLicenseReadListener onLicenseReadListener) {
        this.onLicenseReadListener = onLicenseReadListener;
    }

    /**
     * returns content of licens files
     *
     * @param license one of: mit | lgpl | apache
     * @return license as string
     * @throws IOException
     */
    private String getLicenseText(String license) throws IOException {
        InputStream in;

        if (LicenseActivity.KEY_BU_LICENSE_APACHE.equals(license)) {
            in = this.licenseActivity.getResources().openRawResource(
                    R.raw.apache);
        } else if (LicenseActivity.KEY_BU_LICENSE_LGPL.equals(license)) {
            in = this.licenseActivity.getResources()
                    .openRawResource(R.raw.lgpl);
        } else if (LicenseActivity.KEY_BU_LICENSE_MIT.equals(license)) {
            in = this.licenseActivity.getResources().openRawResource(
                    R.raw.mit);
        } else {
            throw new IllegalArgumentException(
                    "Argument has to be one of: \"apache\",\"lgpl\",\"mit\"");
        }

        Reader fr = new InputStreamReader(in, "utf-8");
        String s = "";
        int read;
        final int BUFF_SIZE = 256;
        char buffer[] = new char[BUFF_SIZE];
        do {
            read = fr.read(buffer, 0, BUFF_SIZE);
            s += String.valueOf(buffer, 0, read);
        } while (read == BUFF_SIZE);
        return s;
    }
}
package com.codingspezis.android.metalonly.player;

import android.annotation.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.licensing.*;

/**
 * displays one of: mit | lgpl | apache
 * TODO use androidannotatons
 */
public class LicenseActivity extends AppCompatActivity {

    // bundle keys
    public static final String KEY_BU_LICENSE_NAME = "MO_LICENSE_NAME";
    public static final String KEY_BU_LICENSE_MIT = "mit";
    public static final String KEY_BU_LICENSE_LGPL = "lgpl";
    public static final String KEY_BU_LICENSE_APACHE = "apache";

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO refactor
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String license = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            license = bundle.getString(KEY_BU_LICENSE_NAME);
        }
        setContentView(R.layout.activity_licensing);
        setTitle(license.toUpperCase());
        final TextView textView = (TextView) findViewById(R.id.license);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        final ThreadedLicenseReader reader = new ThreadedLicenseReader(this,
                license);
        reader.setOnLicenseReadListener(new OnLicenseReadListener() {
            @Override
            public void onLicenseRead() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String license = reader.getLicense();
                        if (license != null) {
                            textView.setText(license);
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        reader.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, StreamControlActivity_.class);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return false;
    }
}

package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codingspezis.android.metalonly.player.utils.FeedbackMailer_;
import com.github.ironjan.metalonly.client_library.MetalOnlyRetrofitApi;
import com.github.ironjan.metalonly.client_library.MetalOnlyRetrofitApiFactory;
import com.github.ironjan.metalonly.client_library.RetrofitStats;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;

@EActivity(R.layout.activity_temporary_test)
@SuppressLint("Registered")
public class TemporaryTestActivity extends AppCompatActivity {
    public static final int SOME_REQUEST = 0;
    public static final int ENABLE_REQUEST = 1;
    public static final int INSTALL_REQUEST = 3;
    public static final String KEY_STATUS_DATA = "KEY_STATUS_DATA";
    @ViewById
    TextView txtStatus;
    @ViewById
    EditText editResult;

    @StringRes
    protected  String app_name;

    @ViewById
    Button btnDoStuff;

    private Queue<String> fails = new LinkedList<>();
    private Queue<String> successes = new LinkedList<>();

    @Click
    void btnDoStuff() {
        String body = editResult.getText().toString();
        FeedbackMailer_.getInstance_(this).sendMail(body);
    }

    @AfterViews
    void executeTests() {
        analyzeSystem();
        doATest();
    }

    @Background
    void analyzeSystem() {
        statusUpdate(app_name + " " + BuildConfig.VERSION_NAME);
        statusUpdate("Android: " + Build.VERSION.RELEASE);
        statusUpdate("Model: " + Build.MODEL);

        analyseAvailableCipherSuites();
    }

    @Background
    void analyseAvailableCipherSuites() {
        final String testName = "CipherSuites";
        statusUpdate("Test: " + testName + " started");
        try {
            SSLEngine engine = SSLContext.getDefault().createSSLEngine();

            String enabledCipherSuites = arrayToString(engine.getEnabledCipherSuites());
            String supportedCipherSuites = arrayToString(engine.getSupportedCipherSuites());

            statusUpdate("Supported Cipher Suites: " + supportedCipherSuites);
            statusUpdate("Enabled Cipher Suites: " + enabledCipherSuites);
        } catch (Exception e) {
            statusUpdate("Could not create SSLEngine: ", e);
        }

        statusUpdate("Test: " + testName + " complete");
    }

    @Background
    void doATest() {
        MetalOnlyRetrofitApi api = new MetalOnlyRetrofitApiFactory(this).build();


        api.getStats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RetrofitStats>() {
                    private final String testName = "A";

                    @Override
                    protected void onStart() {
                        super.onStart();
                        statusUpdate("Test " + testName + ": Started.");

                    }

                    @Override
                    public void onNext(RetrofitStats plan) {
                        statusUpdate("Test " + testName + ": onNext.");
                        handlePlan(plan);
                    }

                    @Override
                    public void onError(Throwable e) {
                        statusUpdate("Test " + testName + ": Error - " + e.getMessage());
                        fails.add(testName);
                        doBTest();
                    }

                    @Override
                    public void onComplete() {
                        statusUpdate("Test " + testName + ": Success.");
                        successes.add(testName);
                        doBTest();
                    }
                });
    }

    @Background
    void doBTest() {
        final String testName = "B";
        try {
            statusUpdate("Test " + testName + ": Started.");
            OkHttpClient client = new OkHttpClient();
            tryOkHttpTest(client, testName);
            statusUpdate("Test " + testName + ": Success.");
            successes.add(testName);
        } catch (Exception e) {
            statusUpdate("Test " + testName + ": Error - ", e);
            fails.add(testName);
        }
        doCTest();
    }

    @Background
    void doCTest() {
        final String testName = "C";
        try {
            statusUpdate("Test " + testName + ": Started");
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .build();
            tryOkHttpTest(client, testName);
            statusUpdate("Test " + testName + ": Success");
            successes.add(testName);

        } catch (Exception e) {
            statusUpdate("Test " + testName + ": Error - ", e);
            fails.add(testName);
        }
        doDTest();
    }

    @Background
    void doDTest() {
        final String testName = "D";
        try {
            statusUpdate("Test " + testName + ": Started");
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384)
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .build();
            tryOkHttpTest(client, testName);
            statusUpdate("Test " + testName + ": Success");
            successes.add(testName);
        } catch (Exception e) {
            statusUpdate("Test " + testName + ": Error - ", e);
            fails.add(testName);
        }

        doETest();
    }

    private void doETest() {
        final String testName = "E";
        try {
            statusUpdate("Test " + testName + ": Started");
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384)
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .build();
            tryOkHttpTest(client, testName);
            statusUpdate("Test " + testName + ": Success");
            successes.add(testName);
        } catch (Exception e) {
            statusUpdate("Test " + testName + ": Error - ", e);
            fails.add(testName);
        }

        testPlayServices();
    }

    @Background
    void testPlayServices() {
        final String testName = "PlayServices";
        statusUpdate("Test " + testName + ": started");


        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int code = googleApi.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            statusUpdate("Test " + testName + ": available, up to date");
            statusUpdate("Test " + testName + ": installing GSM Provider");

            testWithProviderInstaller(googleApi);
        } else {
            statusUpdate("Test " + testName + ": some error");
            if (googleApi.isUserResolvableError(code)) {
                statusUpdate("Test " + testName + ": error is user resolvable, fixing request sent");
                sendPlayServiceInstallRequest(googleApi, code);

            } else {
                statusUpdate("Test " + testName + ": non-recoverable error: " + code);
            }

            finalizeTests();
        }

    }

    @Background
    void testWithProviderInstaller(GoogleApiAvailability googleApi) {
        try {
            ProviderInstaller.installIfNeeded(this);

            String testName = "F";

            try {
                statusUpdate("Test F: Started");
                ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384)
                        .build();

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectionSpecs(Collections.singletonList(spec))
                        .build();
                tryOkHttpTest(client, testName);
                statusUpdate("Test "+testName+": Success");
                successes.add(testName);
            } catch (Exception e) {
                statusUpdate("Test "+testName+": Error - ", e);
                fails.add(testName);
            }
            finalizeTests();
        } catch (GooglePlayServicesRepairableException e) {
            sendPlayServiceInstallRequest(googleApi, e.getConnectionStatusCode());
        } catch (GooglePlayServicesNotAvailableException e) {
            sendPlayServiceInstallRequest(googleApi, e.errorCode);
        }
        finalizeTests();
    }

    @UiThread
    void sendPlayServiceInstallRequest(GoogleApiAvailability googleApi, int code) {
        googleApi.showErrorDialogFragment(this, code, SOME_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ENABLE_REQUEST:
                statusUpdate("onActivityResult(ENABLE, " + resultCode + "..)");
                break;
            case INSTALL_REQUEST:
                statusUpdate("onActivityResult(INSTALL, " + resultCode + "..)");
                break;
            case SOME_REQUEST:
                statusUpdate("onActivityResult(UPDATE, " + resultCode + "..)");
                break;
        }
        statusUpdate("Trying play services test again..");
        testPlayServices();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(KEY_STATUS_DATA, editResult.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        statusUpdate("Restored instance state...");
        statusUpdate(savedInstanceState.getString(KEY_STATUS_DATA));
    }

    private String arrayToString(String[] strings) {
        String s = "";
        for (String fail : strings) {
            s = (s.isEmpty()) ? fail : s + ", " + fail;
        }
        return s;
    }

    private void finalizeTests() {
        String failsAsString = "";
        for (String fail : fails) {
            failsAsString = (failsAsString.isEmpty()) ? fail : failsAsString + ", " + fail;
        }
        String successesAsString = "";
        for (String success : successes) {
            successesAsString = (successesAsString.isEmpty()) ? success : successesAsString + ", " + success;
        }

        statusUpdate("Tests complete (Fails:" + failsAsString + ", Success: " + successesAsString + "). Please send mail.");
    }

    private void tryOkHttpTest(OkHttpClient client, String testName) throws IOException {
        Request request = new Request.Builder()
                .url("https://www.metal-only.de/botcon/mob.php?action=stats")
                .build();

        Response response = client.newCall(request).execute();
        statusUpdate("Test " + testName + ": " + response.code() + "; " + response.body().string());

    }

    private void handlePlan(RetrofitStats plan) {
        statusUpdate(plan.toString());
    }

    Logger logger = LoggerFactory.getLogger(TemporaryTestActivity.class.getName());

    @UiThread
    void statusUpdate(String s) {
        editResult.setText(editResult.getText() + "\n\n" + s);
        txtStatus.setText(s);
        logger.info(s);
    }

    @UiThread
    void statusUpdate(String s, Throwable t) {
        String appends = "\n\n" + s + t.getMessage() + "\n" + t;
        editResult.setText(editResult.getText() + appends);
        txtStatus.setText(s);
        logger.info(s);
    }
}


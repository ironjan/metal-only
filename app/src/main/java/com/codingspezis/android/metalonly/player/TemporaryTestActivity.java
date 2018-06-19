package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codingspezis.android.metalonly.player.utils.FeedbackMailer_;
import com.github.ironjan.metalonly.client_library.MetalOnlyRetrofitApi;
import com.github.ironjan.metalonly.client_library.MetalOnlyRetrofitApiFactory;
import com.github.ironjan.metalonly.client_library.RetrofitStats;

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
    @ViewById
    TextView txtStatus;
    @ViewById
    EditText editResult;

    @ViewById
    Button btnDoStuff;

    @Click
    void btnDoStuff() {
        String body = editResult.getText().toString();
        FeedbackMailer_.getInstance_(this).sendMail(body);
    }

    @AfterViews
    void doATest() {
        MetalOnlyRetrofitApi api = new MetalOnlyRetrofitApiFactory(this).build();


        DisposableObserver<RetrofitStats> obs = new DisposableObserver<RetrofitStats>() {
            @Override
            protected void onStart() {
                super.onStart();
                statusUpdate("Started to load stats.");

            }

            @Override
            public void onNext(RetrofitStats plan) {
                handlePlan(plan);
            }

            @Override
            public void onError(Throwable e) {
                statusUpdate(e.getMessage());
                doBTest();
            }

            @Override
            public void onComplete() {
                statusUpdate("Loaded stats successfully.");
                doBTest();
            }
        };
        api.getStats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(obs);


    }

    @Background
    void doBTest() {
        try {
            statusUpdate("Started test b");
            OkHttpClient client = new OkHttpClient();
            tryOkHttpTest(client);
            statusUpdate("Success: test b");
        } catch (Exception e) {
            statusUpdate("Test B Failed: ", e);
        }
        doCTest();
    }

    @Background
    void doCTest() {
        try {
            statusUpdate("Started test c");
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
            tryOkHttpTest(client);
            statusUpdate("Test C Success!");

        } catch (Exception e) {
            statusUpdate("Test C Failed: ", e);
        }
        finalizeTests();

    }

    private void finalizeTests() {
        statusUpdate("Tests complete. Please send mail.");
    }

    private void tryOkHttpTest(OkHttpClient client) {
        Request request = new Request.Builder()
                .url("https://www.metal-only.de/botcon/mob.php?action=stats")
                .build();

        try (Response response = client.newCall(request).execute()) {
            statusUpdate("Pure OkHTTP: " + response.code() + "; " + response.body().string());
        } catch (IOException e) {
            statusUpdate("OkHttp Error: " + e.getMessage());
        }
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


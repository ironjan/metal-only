package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.codingspezis.android.metalonly.player.fragments.PlanFragment;
import com.codingspezis.android.metalonly.player.siteparser.HTTPDownloadImplementation;
import com.codingspezis.android.metalonly.player.siteparser.OnHTTPGrabberListener;
import com.codingspezis.android.metalonly.player.utils.UrlConstants;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;


@EActivity(R.layout.activity_plan)
@SuppressLint({"SimpleDateFormat", "Registered"})
public class PlanActivity extends AppCompatActivity implements OnHTTPGrabberListener {

    public static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("dd.MM.yy");
    public static final SimpleDateFormat DATE_FORMAT_DATE_DAY = new SimpleDateFormat("dd");
    private final int timeoutDelay = 30000;
    @StringRes
    String plan;
    @StringArrayRes
    String[] days;

    @ViewById(android.R.id.progress)
    View progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    @AfterViews
    void updateTitle() {
        setTitle(plan);
    }

    @AfterInject
    @Background
    void loadPlan(){
        HTTPDownloadImplementation.instance(UrlConstants.API_OLD_PLAN_URL, this, timeoutDelay).download();
    }

    @SuppressLint("InlinedApi")
    @OptionsItem(android.R.id.home)
    void upButtonClicked() {
        Intent intent = new Intent(this, StreamControlActivity_.class);
        NavUtils.navigateUpTo(this, intent);
    }

    @UiThread
    void toastMessage(final Context context, final String msg) {
        (new Handler(context.getMainLooper())).post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onSuccess(BufferedReader httpResponse) {
        String site = "";
        String line;
        try {
            while ((line = httpResponse.readLine()) != null) {
                site += line;
            }
            onPlanLoaded(site);

        } catch (IOException e) {
            e.printStackTrace();
            onError(e.getMessage());
            // TODO show error message instead of closing...
            finish();
        }



    }

    @UiThread
    void onPlanLoaded(String site) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, PlanFragment.newInstance(site))
                .commit();
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onTimeout() {
        toastMessage(this, getResources().getString(R.string.plan_timeout));
    }

    @Override
    public void onCancel() {
        toastMessage(this, getResources().getString(R.string.plan_load_cancelled));
    }

    @Override
    public void onError(String error) {
        toastMessage(this, error);
    }
}

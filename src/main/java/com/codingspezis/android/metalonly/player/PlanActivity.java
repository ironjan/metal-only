package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.codingspezis.android.metalonly.player.fragments.PlanFragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

import java.text.SimpleDateFormat;


@EActivity(R.layout.activity_plan)
@SuppressLint({"SimpleDateFormat", "Registered"})
public class PlanActivity extends AppCompatActivity {

    public static final String KEY_SITE = "site";
    public static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("dd.MM.yy");
    public static final SimpleDateFormat DATE_FORMAT_DATE_DAY = new SimpleDateFormat("dd");
    @StringRes
    String plan;
    @StringArrayRes
    String[] days;
    @Extra
    String site;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    @AfterInject
    void afterInject() {
        setTitle(plan);
    }

    @AfterViews
    void bindPlanFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, PlanFragment.newInstance(site))
                .commit();
    }

    @SuppressLint("InlinedApi")
    @OptionsItem(android.R.id.home)
    void upButtonClicked() {
        Intent intent = new Intent(this, StreamControlActivity_.class);
        NavUtils.navigateUpTo(this, intent);
    }

}

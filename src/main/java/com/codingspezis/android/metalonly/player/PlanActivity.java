package com.codingspezis.android.metalonly.player;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.codingspezis.android.metalonly.player.fragments.PlanFragment;
import com.codingspezis.android.metalonly.player.fragments.PlanFragment_;
import com.codingspezis.android.metalonly.player.plan.*;

import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.*;

import java.text.*;
import java.util.*;


/**
 * FIXME Replace Sherlock component
 */
@EActivity(R.layout.activity_plan)
@SuppressLint({"SimpleDateFormat", "Registered"})
public class PlanActivity extends AppCompatActivity {

    public static final String KEY_SITE = "site";
    public static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat         DATE_FORMAT_DATE = new SimpleDateFormat("dd.MM.yy");
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @AfterInject
    void afterInject() {
        setTitle(plan);
    }

    @AfterViews
    void bindPlanFragment(){
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        PlanFragment build = PlanFragment.newInstance(site);
        ft.replace(android.R.id.content, build);
        ft.commit();
    }
    @SuppressLint("InlinedApi")
    @OptionsItem(android.R.id.home)
    void upButtonClicked() {
        Intent intent = new Intent(this, StreamControlActivity_.class);
        NavUtils.navigateUpTo(this, intent);
    }

}

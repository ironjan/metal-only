package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ListView;

import com.codingspezis.android.metalonly.player.fragments.FavoritesFragment;
import com.codingspezis.android.metalonly.player.fragments.FavoritesFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

/**
 * Activity that displays favorites and allows to handle them
 */
@EActivity(R.layout.activity_favorites)
@SuppressLint("Registered")
public class FavoritesActivity extends AppCompatActivity {

    public static final String JSON_FILE_FAV = "mo_fav.json";
    @ViewById
    ListView list;

    @AfterViews
    void bindFragment() {
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        FavoritesFragment build = FavoritesFragment_.builder().build();
        ft.replace(android.R.id.content, build);
        ft.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @SuppressLint("InlinedApi")
    @OptionsItem(android.R.id.home)
    void upButtonClicked() {
        Intent intent = new Intent(this, StreamControlActivity_.class);
        NavUtils.navigateUpTo(this, intent);
    }
}

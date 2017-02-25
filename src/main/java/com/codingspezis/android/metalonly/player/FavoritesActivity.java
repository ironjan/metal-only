package com.codingspezis.android.metalonly.player;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.DialogInterface.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.favorites.*;
import com.codingspezis.android.metalonly.player.fragments.FavoritesFragment;
import com.codingspezis.android.metalonly.player.fragments.FavoritesFragment_;
import com.codingspezis.android.metalonly.player.fragments.PlanFragment;
import com.codingspezis.android.metalonly.player.fragments.PlanFragment_;
import com.codingspezis.android.metalonly.player.siteparser.*;
import com.codingspezis.android.metalonly.player.utils.UrlConstants;
import com.codingspezis.android.metalonly.player.wish.*;

import org.androidannotations.annotations.*;

import java.net.*;
import java.util.*;

/**
 * FavoritesActivity
 * <p/>
 * this activity displays favorites and allows to handle them
 */
@EActivity(R.layout.activity_favorites)
@OptionsMenu(R.menu.favoritesmenu)
@SuppressLint("Registered")
public class FavoritesActivity extends AppCompatActivity {

    public static final String JSON_FILE_FAV = "mo_fav.json";
    @ViewById
    ListView list;

    private Menu menu;

    @AfterViews
    void bindFragment(){
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



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (event.getAction() == KeyEvent.ACTION_UP && menu != null
                    && menu.findItem(R.id.mnu_sub) != null) {
                menu.performIdentifierAction(R.id.mnu_sub, 0);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @SuppressLint("InlinedApi")
    @OptionsItem(android.R.id.home)
    void upButtonClicked() {
        Intent intent = new Intent(this, StreamControlActivity_.class);
        NavUtils.navigateUpTo(this, intent);
    }
}

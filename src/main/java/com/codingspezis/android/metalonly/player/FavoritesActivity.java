package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.ListView;

import com.codingspezis.android.metalonly.player.fragments.FavoritesFragment;
import com.codingspezis.android.metalonly.player.fragments.FavoritesFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

/**
 * Displays favorites and allows to handle them
 * FIXME Replace Sherlock component
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

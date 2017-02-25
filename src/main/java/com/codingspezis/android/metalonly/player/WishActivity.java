package com.codingspezis.android.metalonly.player;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.codingspezis.android.metalonly.player.fragments.*;
import com.codingspezis.android.metalonly.player.wish.*;

import org.slf4j.*;

/**
 * activity that is showing a GUI for entering and sending wishes and/or regards
 * to metal-only.de
 * TODO use androidannotations
 * FIXME Replace Sherlock component
 */

public class WishActivity extends AppCompatActivity {

    // intent keys
    public static final String KEY_WISHES_ALLOWED = "MO_WISHES_ALLOWED";
    public static final String KEY_REGARDS_ALLOWED = "MO_REGARDS_ALLOWED";
    public static final String KEY_NUMBER_OF_WISHES = "MO_NUMBER_OF_WISHES";
    public static final String KEY_DEFAULT_INTERPRET = "MO_DEFAULT_INTERPRET";
    public static final String KEY_DEFAULT_TITLE = "MO_DEFAULT_TITLE";

    private static final Logger LOGGER = LoggerFactory.getLogger(WishActivity.class.getSimpleName());

    /**
     * checks if it is possible to wish something
     *
     * @param allowedActions class for representing what is allowed
     * @return true if you can wish - false otherwise
     */
    public static boolean canWishOrDisplayNot(Activity activity,
                                              AllowedActions allowedActions) {
        if (BuildConfig.DEBUG) LOGGER.debug("");
        if (!allowedActions.moderated) {
            StreamControlActivity.alertMessage(activity,
                    activity.getString(R.string.no_moderator));
            return false;
        } else if (!allowedActions.wishes) {
            StreamControlActivity.alertMessage(activity,
                    activity.getString(R.string.no_wishes));
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) LOGGER.debug("onCreate({})", savedInstanceState);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wish);


        showContentFragment();

        if (BuildConfig.DEBUG) LOGGER.debug("onCreate({}) done", savedInstanceState);
    }

    private void showContentFragment() {
        if (BuildConfig.DEBUG) LOGGER.debug("showContentFragment()");

        Bundle bundle = getIntent().getExtras();
        WishFragment fragment = WishFragment.newInstance(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, fragment);
        ft.commit();

        if (BuildConfig.DEBUG) LOGGER.debug("showContentFragment() done");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (BuildConfig.DEBUG) LOGGER.debug("");
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, StreamControlActivity_.class);
                NavUtils.navigateUpTo(this, intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
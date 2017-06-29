package com.codingspezis.android.metalonly.player;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.codingspezis.android.metalonly.player.core.HistoricTrack;
import com.codingspezis.android.metalonly.player.favorites.SongSaver;
import com.codingspezis.android.metalonly.player.siteparser.HTTPGrabber;
import com.codingspezis.android.metalonly.player.stream.MainBroadcastReceiver;
import com.codingspezis.android.metalonly.player.stream.PlayerService;
import com.codingspezis.android.metalonly.player.stream.SongAdapter;
import com.codingspezis.android.metalonly.player.stream.metadata.Metadata;
import com.codingspezis.android.metalonly.player.stream.metadata.MetadataFactory;
import com.codingspezis.android.metalonly.player.stream.track_info.ShowInfoIntentConstants;
import com.codingspezis.android.metalonly.player.utils.FeedbackMailer;
import com.codingspezis.android.metalonly.player.utils.UrlConstants;
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPIWrapper;
import com.codingspezis.android.metalonly.player.utils.jsonapi.NoInternetException;
import com.codingspezis.android.metalonly.player.views.ShowInformation;
import com.github.ironjan.metalonly.client_library.Stats;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * main GUI activity
 * TODO use androidannotatons
 * TODO move more functionality out of this class
 */
@EActivity(R.layout.activity_stream)
@OptionsMenu(R.menu.mainmenu)
@SuppressLint("Registered")
public class StreamControlActivity extends AppCompatActivity {

    // intent keys
    public static final String showToastMessage = "MO_SHOW_TOAST";
    // shared preferences keys
    public static final String KEY_SP_MODTHUMBDATE = "MO_SP_MODTHUMBDATE_";
    final static long MIN_BOTTON_DELAY = 1000;
    private static final String TAG = StreamControlActivity.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(TAG);
    public static final int LIST_ITEM_ACTION_FAVORITES = 0;
    public static final int LIST_ITEM_ACTION_YOUTUBE = 1;
    public static final int LIST_ITEM_ACTION_SHARE = 2;
    // button is not usable for MIN_BOTTON_DELAY msecs
    static long lastButtonToggle = 0;
    // GUI objects
    private final StreamControlActivity streamControlActivity = this;
    @Bean
    MetalOnlyAPIWrapper apiWrapper;
    @ViewById(android.R.id.list)
    ListView listView;
    @ViewById(R.id.buttonPlay)
    ImageView buttonStream;
    @ViewById(R.id.viewShowInformation)
    ShowInformation viewShowInformation;
    @ViewById(android.R.id.empty)
    View empty;

    @Bean
    FeedbackMailer feedbackMailer;

    Menu menu;
    // other
    private MainBroadcastReceiver broadcastReceiver;
    private Metadata metadata;
    private SongSaver favoritesSaver;
    private SongSaver historySaver;
    // other variables
    private boolean shouldPlay = false;
    private BroadcastReceiver showInfoBroadcastReceiver;

    /**
     * @param context
     * @param msg
     */
    public static void toastMessage(final Context context, final String msg) {
        if (BuildConfig.DEBUG) LOGGER.debug("toastMessage({},{})", context, msg);

        (new Handler(context.getMainLooper())).post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
        if (BuildConfig.DEBUG) LOGGER.debug("toastMessage({},{}) done", context, msg);
    }

    /**
     * @param context
     * @param msg
     */
    public static void alertMessage(final Context context, final String msg) {
        if (BuildConfig.DEBUG) LOGGER.debug("alertMessage({},{})", context, msg);

        (new Handler(context.getMainLooper())).post(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage(msg);
                alert.setPositiveButton(context.getString(R.string.ok), null);
                alert.show();
            }
        });
        if (BuildConfig.DEBUG) LOGGER.debug("alertMessage({},{}) done", context, msg);
    }

    /**
     * initializes GUI objects of main activity
     */
    private void setUpGUIObjects() {
        if (BuildConfig.DEBUG) LOGGER.debug("setUpGUIObjects()");

        listView.setEmptyView(empty);
        toggleStreamButton(false);
        displaySongs();

        setMetadata(MetadataFactory.INSTANCE.getDEFAULT_METADATA());
        if (BuildConfig.DEBUG) LOGGER.debug("setUpGUIObjects() done");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) LOGGER.debug("onCreate({})", savedInstanceState);

        // TODO is setTheme necessary?
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setHomeButtonEnabled(false);
        setSupportProgressBarIndeterminateVisibility(false);

        if (BuildConfig.DEBUG) LOGGER.debug("onCreate({}) done", savedInstanceState);
    }

    @AfterViews
    void afterViews() {
        setUpBroadcastReceiver();
        setUpPlayerService();
        setUpDataObjects();
        setUpGUIObjects();
        if(BuildConfig.DEBUG){
            buttonPlayClicked();
        }
    }

    @Override
    public void onPause() {
        if (BuildConfig.DEBUG) LOGGER.debug("onPause()");
        favoritesSaver.saveSongsToStorage();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (BuildConfig.DEBUG) LOGGER.debug("onResume()");

        super.onResume();
        favoritesSaver.reload();
        refreshShowInfo();
    }

    public void refreshShowInfo() {
        if (BuildConfig.DEBUG) LOGGER.debug("refreshShowInfo()");

        Runnable runnable = new Runnable() {

            private static final String TAG = "Runnable - refreshShowInfo()";
            private final Logger LOGGER = LoggerFactory.getLogger(TAG);

            @Override
            public void run() {
                if (BuildConfig.DEBUG) LOGGER.debug("run()");
                try {
                    Stats stats = apiWrapper.getStats();
                    String moderator = stats.getModerator();
                    String genre = stats.getGenre();
                    updateShowInfo(moderator, genre);
                } catch (NoInternetException e) {
                    // FIXME show share to the user...
                }
            }

            private void updateShowInfo(final String moderator, final String genre) {
                if (BuildConfig.DEBUG) LOGGER.debug("updateShowInfo({},{})", moderator, genre);

                Runnable runnable = new Runnable() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void run() {
                        if(viewShowInformation != null) viewShowInformation.setMetadata(MetadataFactory.INSTANCE.createMetadata(moderator, genre, "", ""));

                        setWishButtonEnabled(!moderator.toLowerCase().startsWith("metalhead"));
                    }
                };
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(runnable);
            }
        };
        new Thread(runnable).start();

    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) LOGGER.debug("onDestroy()");
        Intent tmpIntent = new Intent(PlayerService.INTENT_EXIT);
        sendBroadcast(tmpIntent);
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(showInfoBroadcastReceiver);
        super.onDestroy();
    }

    /**
     * sets up data objects
     */
    private void setUpDataObjects() {
        if (BuildConfig.DEBUG) LOGGER.debug("setUpDataObjects()");
        favoritesSaver = new SongSaver(this, FavoritesActivity.JSON_FILE_FAV,
                -1);
        setMetadata(MetadataFactory.INSTANCE.getDEFAULT_METADATA());
    }

    /**
     * initializes main broadcast receiver with filters
     */
    private void setUpBroadcastReceiver() {
        if (BuildConfig.DEBUG) LOGGER.debug("setUpBroadcastReceiver()");
        broadcastReceiver = new MainBroadcastReceiver(this);
        registerReceiver(broadcastReceiver, new IntentFilter(
                PlayerService.INTENT_STATUS));
        registerReceiver(broadcastReceiver, new IntentFilter(showToastMessage));

        showInfoBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // FIXME do usefult hings
//                setMetadata(MetadataFactory.INSTANCE.createFromString(metadata));
                refreshShowInfo();
                displaySongs();
            }
        };
        registerReceiver(showInfoBroadcastReceiver, ShowInfoIntentConstants.INSTANCE.getIntentFilter());
    }

    /**
     * initializes player service and requests status
     */
    private void setUpPlayerService() {
        if (BuildConfig.DEBUG) LOGGER.debug("setUpPlayerService()");
        Intent playerStartIntent = new Intent(getApplicationContext(),
                PlayerService.class);
        startService(playerStartIntent);
        Intent statusIntent = new Intent(PlayerService.INTENT_STATUS_REQUEST);
        sendBroadcast(statusIntent);
    }

    /**
     * sets the wish / regard button to en- or disabled
     */
    private void setWishButtonEnabled(boolean enabled) {
        if (BuildConfig.DEBUG) LOGGER.debug("setWishButtonEnabled({})", enabled);

        ImageButton btnWish = (ImageButton) findViewById(R.id.btnWish);
        setImageButtonEnabled(this, enabled, btnWish, R.drawable.mo_pen);
        if (BuildConfig.DEBUG) LOGGER.debug("setWishButtonEnabled({}) done", enabled);
    }

    /**
     * THIS METHOD WAS FOUND AT:
     * http://stackoverflow.com/questions/8196206/disable-an-imagebutton
     * <p/>
     * Sets the image button to the given state and grays-out the icon.
     *
     * @param enabled   The state of the button
     * @param item      The button item to modify
     * @param iconResId The button's icon ID
     */
    private void setImageButtonEnabled(Context ctxt, boolean enabled, ImageButton item, int iconResId) {
        if (BuildConfig.DEBUG)
            LOGGER.debug("setImageButtonEnabled({},{},{},{})", new Object[]{ctxt, enabled, item, iconResId});

        item.setEnabled(enabled);
        Drawable originalIcon = ctxt.getResources().getDrawable(iconResId);
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
        item.setImageDrawable(icon);
        if (BuildConfig.DEBUG)
            LOGGER.debug("setImageButtonEnabled({},{},{},{}) done", new Object[]{ctxt, enabled, item, iconResId});
    }

    /**
     * THIS METHOD WAS FOUND AT:
     * http://stackoverflow.com/questions/8196206/disable-an-imagebutton
     * <p/>
     * Mutates and applies a filter that converts the given drawable to a Gray
     * image. This method may be used to simulate the color of disable icons in
     * Honeycomb's ActionBar.
     *
     * @return a mutated version of the given drawable with a color filter applied.
     */
    private Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (BuildConfig.DEBUG) LOGGER.debug("convertDrawableToGrayScale({})", drawable);

        if (drawable == null)
            return null;
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, Mode.SRC_IN);
        if (BuildConfig.DEBUG) LOGGER.debug("convertDrawableToGrayScale({}) done", drawable);
        return res;
    }

    /**
     * displays history songs on screen
     */
    public void displaySongs() {
        if (BuildConfig.DEBUG) LOGGER.debug("displaySongs()");

        historySaver = new SongSaver(this, PlayerService.JSON_FILE_HIST,
                PlayerService.MAXIMUM_NUMBER_OF_HISTORY_SONGS);
        listView.removeAllViewsInLayout();
        ArrayList<HistoricTrack> data = new ArrayList<>();

        for (int i = historySaver.size() - 1; i >= 0; i--) {
            data.add(historySaver.get(i));
        }

        SongAdapter adapter = new SongAdapter(this, data);
        listView.setAdapter(adapter);
        if (BuildConfig.DEBUG) LOGGER.debug("displaySongs() done");
    }

    /**
     * toggles background color of stream button
     *
     * @param listening if this value is true the button shows stop from now on;
     *                  otherwise play is false
     */
    public void toggleStreamButton(boolean listening) {
        if (BuildConfig.DEBUG) LOGGER.debug("toggleStreamButton({})", listening);

        if (listening) {
            buttonStream.setImageResource(R.drawable.mo_stop5);
        } else {
            buttonStream.setImageResource(R.drawable.mo_play5);
        }
        if (BuildConfig.DEBUG) LOGGER.debug("toggleStreamButton({}) done", listening);
    }

    @OptionsItem(R.id.mnu_donation)
    void startDonation() {
        Intent paypalIntent = new Intent(getApplicationContext(), PayPalDonationActivity.class);
        startActivity(paypalIntent);
    }

    @OptionsItem(R.id.mnu_info)
    void startAbout() {
        AboutActivity_.intent(this).start();
    }

    @OptionsItem(R.id.mnu_favorites)
    void startFavorites() {
        FavoritesActivity_.intent(this).start();
    }

    @OptionsItem(R.id.mnu_feedback)
    void sendFeedback() {
        feedbackMailer.sendEmail();
    }

    @Click
    void buttonPlayClicked() {
        long currentTime = System.currentTimeMillis();
        boolean hasWaitedLongEnoughToClickAgain = (MIN_BOTTON_DELAY <= currentTime - lastButtonToggle);
        if (hasWaitedLongEnoughToClickAgain) {
            lastButtonToggle = System.currentTimeMillis();
            if (isShouldPlay()) {
                stopListening();
            } else {
                if (!HTTPGrabber.displayNetworkSettingsIfNeeded(this)) {
                    startListening();
                }
            }
        }
    }

    @Click
    void btnCalendarClicked() {
        if (!HTTPGrabber.displayNetworkSettingsIfNeeded(this)) {
            startPlanActivity();
        }
    }

    @Click
    void btnWishClicked() {
        if (!HTTPGrabber.displayNetworkSettingsIfNeeded(this)) {
            tryStartWishActivity();
        }
    }

    private void startPlanActivity() {
        PlanActivity_.intent(this).start();
    }

    @Background
    void tryStartWishActivity() {
        if (BuildConfig.DEBUG) LOGGER.debug("tryStartWishActivity()");

        Stats stats = apiWrapper.getStats();
        if (stats.isNotModerated()) {
            alertMessage(streamControlActivity,
                    streamControlActivity.getString(R.string.no_moderator));
        } else if (stats.canNeitherWishNorGreet()) {
            alertMessage(streamControlActivity, streamControlActivity
                    .getString(R.string.no_wishes_and_regards));
        } else {
            Intent wishIntent = new Intent(streamControlActivity, WishActivity.class);
            streamControlActivity.startActivity(wishIntent);
        }
        if (BuildConfig.DEBUG) LOGGER.debug("tryStartWishActivity() done");
    }

    /**
     * sets listening to true sends getStartDate intent to PlayerService shows
     * connecting dialog
     */
    private void startListening() {
        if (BuildConfig.DEBUG) LOGGER.debug("startListening()");

        setSupportProgressBarIndeterminateVisibility(true);
        setShouldPlay(true);
        toggleStreamButton(true);
        Intent tmpIntent = new Intent(PlayerService.INTENT_PLAY);
        sendBroadcast(tmpIntent);
        if (BuildConfig.DEBUG) LOGGER.debug("startListening()");
    }

    /**
     * sets listening to false sends stop intent to PlayerService
     */
    public void stopListening() {
        if (BuildConfig.DEBUG) LOGGER.debug("stopListening()");

        setSupportProgressBarIndeterminateVisibility(false);
        setShouldPlay(false);
        toggleStreamButton(false);
        Intent tmpIntent = new Intent(PlayerService.INTENT_STOP);
        sendBroadcast(tmpIntent);
        if (BuildConfig.DEBUG) LOGGER.debug("stopListening() done");


    }

    /**
     * displays meta data
     */
    public void displayMetadata() {
        if (BuildConfig.DEBUG) LOGGER.debug("displayMetadata()");

        Metadata metadata = getMetadata();
        if (metadata.historicTrack().isValid() && isShouldPlay()) {
            if(viewShowInformation != null) viewShowInformation.setMetadata(metadata); //NOPMD This will be optimized automatically by the kotlin converter
        }
        if (BuildConfig.DEBUG) LOGGER.debug("displayMetadata() done");

    }

    @ItemClick(android.R.id.list)
    void listItemClicked(int position) {
        if (BuildConfig.DEBUG) LOGGER.debug("listItemClicked({})", position);

        final int index = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.history_options_array,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int action) {
                        handleAction(historySaver.size() - index - 1, action);
                    }
                }
        );
        builder.show();
        if (BuildConfig.DEBUG) LOGGER.debug("listItemClicked({}) done", position);
    }

    /**
     * handles an action on an index
     *
     * @param index  item to handle
     * @param action action to handle
     */
    private void handleAction(final int index, int action) {
        if (BuildConfig.DEBUG) LOGGER.debug("handleAction({},{})", index, action);

        switch (action) {
            case LIST_ITEM_ACTION_FAVORITES: // add to favorites
                HistoricTrack track = historySaver.get(index);
                if (favoritesSaver.isAlreadyIn(track) == -1) {
                    favoritesSaver.addSong(track.withClearedThumb());
                    Toast.makeText(this, R.string.fav_added, Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(this, R.string.fav_already_in, Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case LIST_ITEM_ACTION_YOUTUBE: // YouTube
                String searchStr = historySaver.get(index).getArtist() + " - "
                        + historySaver.get(index).getTitle();
                try {
                    searchStr = URLEncoder.encode(searchStr, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri url = Uri.parse(UrlConstants.INSTANCE.getYOUTUBE_SEARCH_URL()
                        + searchStr);
                Intent youtube = new Intent(Intent.ACTION_VIEW, url);
                startActivity(youtube);
                break;
            case LIST_ITEM_ACTION_SHARE: // share
                String message = historySaver.get(index).getArtist() + " - "
                        + historySaver.get(index).getTitle();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, getResources()
                        .getStringArray(R.array.favorite_options_array)[2]));
                break;
        }
        if (BuildConfig.DEBUG) LOGGER.debug("handleAction({},{}) done", index, action);

    }

    public boolean isShouldPlay() {
        return shouldPlay;
    }

    public void setShouldPlay(boolean shouldPlay) {
        this.shouldPlay = shouldPlay;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @AfterInject
    @Background
    void loadShowData(){
        try{
            displayShowData(apiWrapper.getStats());
        } catch(NoInternetException e){
          toastMessage(this, getResources().getString(R.string.no_internet));
        } catch(Exception e){
            e.printStackTrace();
            toastMessage(this, e.getMessage());
        }
    }

    @UiThread
    void displayShowData(Stats stats) {
        viewShowInformation.setStats(stats);
    }
}

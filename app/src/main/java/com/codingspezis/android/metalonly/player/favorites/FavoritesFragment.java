package com.codingspezis.android.metalonly.player.favorites;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.codingspezis.android.metalonly.player.BuildConfig;
import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.WishActivity;
import com.codingspezis.android.metalonly.player.core.HistoricTrack;
import com.codingspezis.android.metalonly.player.siteparser.HTTPGrabber;
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPIWrapper;
import com.codingspezis.android.metalonly.player.utils.jsonapi.Stats;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Displays favorites and allows to handle them
 */
@EFragment(R.layout.fragment_favorites)
@OptionsMenu(R.menu.favoritesmenu)
@SuppressLint("Registered")
public class FavoritesFragment extends Fragment {

    public static final String JSON_FILE_FAV = "mo_fav.json";
    private static final int ITEM_CLICK_ACTION_DELETE = 3;
    private static final int ITEM_CLICK_ACTION_SHARE = 2;
    private static final int ITEM_CLICK_ACTION_YOUTUBE = 1;
    private static final int ITEM_CLICK_ACTION_WISH = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(FavoritesFragment.class);
    @ViewById
    ListView list;
    @ViewById
    View empty;

    @Bean
    MetalOnlyAPIWrapper apiWrapper;
    private SongAdapterFavorites adapter;

    private SongSaver favoritesSaver;

    /**
     * asks if user is sure to delete share
     *
     * @param yes what is to do if user clicks yes
     * @param no  what is to do if user clicks no
     */
    public static void askSureDelete(Context context, OnClickListener yes, OnClickListener no) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(R.string.delete_sure);
        alert.setNegativeButton(R.string.no, no);
        alert.setPositiveButton(R.string.yes, yes);
        alert.show();
    }

    @AfterViews
    void bindContent(){
        favoritesSaver = new SongSaver(getActivity(), JSON_FILE_FAV, -1);
        adapter = new SongAdapterFavorites(getActivity(), new ArrayList<HistoricTrack>(0));
        list.setAdapter(adapter);
        list.setEmptyView(empty);
        displayFavorites();
    }
    @Override
    public void onPause() {
        favoritesSaver.saveSongsToStorage();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        favoritesSaver.reload();
        displayFavorites();
    }

    @OptionsItem(R.id.mnu_add_manually)
    void showAddSongDialog() {
        // TODO We should use a custom class for this dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.menu_add_mannually);

        // FIXME fix this...
        @SuppressLint("RestrictedApi")
        final View v = getLayoutInflater(null).inflate(R.layout.dialog_add_song, null);

        alert.setView(v);
        alert.setNegativeButton(R.string.abort, null);
        alert.setPositiveButton(R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int action) {
                EditText artist = (EditText) v.findViewById(R.id.edit_artist);
                EditText txtTitle = (EditText) v.findViewById(R.id.edit_title);

                String interpret = artist.getText().toString();
                String title = txtTitle.getText().toString();

                HistoricTrack song = new HistoricTrack(interpret, title, "", Calendar.getInstance().getTimeInMillis());

                if (song.isValid() && favoritesSaver.isAlreadyIn(song) < 0) {
                    favoritesSaver.addSong(song);
                    displayFavorites();
                }
            }
        });
        alert.show();
    }

    /**
     * displays favorites on screen
     */
    private void displayFavorites() {
        list.removeAllViewsInLayout();
        ArrayList<HistoricTrack> songs = new ArrayList<>();
        for (int i = favoritesSaver.size() - 1; i >= 0; i--) {
            songs.add(favoritesSaver.get(i));
        }
        adapter.replaceData(songs);
    }

    /**
     * handles an action on an index
     *
     * @param index  item to handle
     * @param action action to handle
     */
    private void handleAction(final int index, int action) {
        switch (action) {
            case ITEM_CLICK_ACTION_WISH: // wish
                wishSong(index);
                break;
            case ITEM_CLICK_ACTION_YOUTUBE: // YouTube
                searchSongOnYoutube(index);
                break;
            case ITEM_CLICK_ACTION_SHARE: // share
                shareSong(index);
                break;
            case ITEM_CLICK_ACTION_DELETE: // delete
                deleteSong(index);
                break;
            default:
                /* unknown action, ignore */
        }
    }

    @Background
    void wishSong(final int index) {
        if (!HTTPGrabber.displayNetworkSettingsIfNeeded(getActivity())) {
            Stats stats = apiWrapper.getStats();
            if (stats.isNotModerated()) {
                alertMessage(getActivity(), getString(R.string.no_moderator));
            } else if (stats.canNeitherWishNorGreet()) {
                alertMessage(getActivity(), getString(R.string.no_wishes_and_regards));
            } else {
                // FIXME replace this with android annotation intent call (Wishactivity is not AA yet!)
                Bundle bundle = new Bundle();
                bundle.putString(WishActivity.KEY_DEFAULT_INTERPRET, favoritesSaver.get(index).getArtist());
                bundle.putString(WishActivity.KEY_DEFAULT_TITLE, favoritesSaver.get(index).getTitle());
                Intent wishIntent = new Intent(getActivity(), WishActivity.class);
                wishIntent.putExtras(bundle);

                getActivity().startActivity(wishIntent);
            }
        }
    }


    @UiThread
    void alertMessage(final Context context, final String msg) {
        if (BuildConfig.DEBUG) LOGGER.debug("alertMessage({},{})", context, msg);

        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton(context.getString(R.string.ok), null)
                .show();

        if (BuildConfig.DEBUG) LOGGER.debug("alertMessage({},{}) done", context, msg);
    }

    private void searchSongOnYoutube(final int index) {
        String searchStr = favoritesSaver.get(index).getArtist() + " - "
                + favoritesSaver.get(index).getTitle();
        try {
            searchStr = URLEncoder.encode(searchStr, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri url = Uri.parse("http://www.youtube.com/results?search_query=" + searchStr);
        Intent youtube = new Intent(Intent.ACTION_VIEW, url);
        startActivity(youtube);
    }

    private void shareSong(final int index) {
        String message = favoritesSaver.get(index).getArtist() + " - "
                + favoritesSaver.get(index).getTitle();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share,
                getResources().getStringArray(R.array.favorite_options_array)[2]));
    }

    private void deleteSong(final int index) {
        favoritesSaver.removeAt(index);
        displayFavorites();
    }

    @ItemClick(android.R.id.list)
    public void listItemClicked(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.favorite_options_array, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int action) {
                handleAction(favoritesSaver.size() - position - 1, action);
            }
        });
        builder.show();
    }

    @OptionsItem(R.id.mnu_deleteall)
    void deleteAllClicked() {
        askSureDelete(getActivity(), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int action) {
                favoritesSaver.clear();
                displayFavorites();
            }
        }, null);
    }

    @OptionsItem(R.id.mnu_shareall)
    void shareAllClicked() {
        String message = "";
        for (int i = favoritesSaver.size() - 1; i >= 0; i--) {
            message += favoritesSaver.get(i).getArtist() + " - " + favoritesSaver.get(i).getTitle() + "\n";
        }
        // open share dialog
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share,
                getResources().getStringArray(R.array.favorite_options_array)[2]));
    }

}

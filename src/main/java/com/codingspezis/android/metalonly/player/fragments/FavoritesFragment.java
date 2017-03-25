package com.codingspezis.android.metalonly.player.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.WishActivity;
import com.codingspezis.android.metalonly.player.favorites.Song;
import com.codingspezis.android.metalonly.player.favorites.SongAdapterFavorites;
import com.codingspezis.android.metalonly.player.favorites.SongSaver;
import com.codingspezis.android.metalonly.player.siteparser.HTTPGrabber;
import com.codingspezis.android.metalonly.player.utils.UrlConstants;
import com.codingspezis.android.metalonly.player.wish.AllowedActions;
import com.codingspezis.android.metalonly.player.wish.OnWishesCheckedListener;
import com.codingspezis.android.metalonly.player.wish.WishChecker;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Displays favorites and allows to handle them
 */
@EFragment(R.layout.fragment_favorites)
@OptionsMenu(R.menu.favoritesmenu)
@SuppressLint("Registered")
public class FavoritesFragment extends ListFragment {

    public static final String JSON_FILE_FAV = "mo_fav.json";
    private static final int ITEM_CLICK_ACTION_DELETE = 3;
    private static final int ITEM_CLICK_ACTION_SHARE = 2;
    private static final int ITEM_CLICK_ACTION_YOUTUBE = 1;
    private static final int ITEM_CLICK_ACTION_WISH = 0;
    @ViewById
    ListView list;

    private SongSaver favoritesSaver;

    /**
     * asks if user is sure to delete something
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favoritesSaver = new SongSaver(getActivity(), JSON_FILE_FAV, -1);
        list = getListView();
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
        final View v = getLayoutInflater(null).inflate(R.layout.dialog_add_song, null);
        alert.setView(v);
        alert.setNegativeButton(R.string.abort, null);
        alert.setPositiveButton(R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText artist = (EditText) v.findViewById(R.id.edit_artist);
                EditText txtTitle = (EditText) v.findViewById(R.id.edit_title);

                String interpret = artist.getText().toString();
                String title = txtTitle.getText().toString();

                Song song = new Song(interpret, title);

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
        ArrayList<Song> songs = new ArrayList<>();
        for (int i = favoritesSaver.size() - 1; i >= 0; i--) {
            songs.add(favoritesSaver.get(i));
        }
        SongAdapterFavorites adapter = new SongAdapterFavorites(getActivity(), songs);
        list.setAdapter(adapter);
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

    private void wishSong(final int index) {
        if (!HTTPGrabber.displayNetworkSettingsIfNeeded(getActivity())) {
            WishChecker wishChecker = new WishChecker(getActivity(), UrlConstants.METAL_ONLY_WISHES_WISHES_URL);
            wishChecker.setOnWishesCheckedListener(new OnWishesCheckedListener() {
                @Override
                public void onWishesChecked(AllowedActions allowedActions) {
                    // TODO simplify intent
                    if (WishActivity
                            .canWishOrDisplayNot(getActivity(), allowedActions)) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(WishActivity.KEY_WISHES_ALLOWED,
                                allowedActions.getWishes());
                        bundle.putBoolean(WishActivity.KEY_REGARDS_ALLOWED,
                                allowedActions.getRegards());
                        bundle.putString(WishActivity.KEY_NUMBER_OF_WISHES,
                                allowedActions.getLimit());
                        bundle.putString(WishActivity.KEY_DEFAULT_INTERPRET,
                                favoritesSaver.get(index).interpret);
                        bundle.putString(WishActivity.KEY_DEFAULT_TITLE,
                                favoritesSaver.get(index).title);
                        Intent wishIntent = new Intent(getActivity(), WishActivity.class);
                        wishIntent.putExtras(bundle);
                        FavoritesFragment.this.startActivity(wishIntent);
                    }
                }
            });
            wishChecker.start();
        }
    }

    private void searchSongOnYoutube(final int index) {
        String searchStr = favoritesSaver.get(index).interpret + " - "
                + favoritesSaver.get(index).title;
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
        String message = favoritesSaver.get(index).interpret + " - "
                + favoritesSaver.get(index).title;
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
            public void onClick(DialogInterface dialog, int which) {
                handleAction(favoritesSaver.size() - position - 1, which);
            }
        });
        builder.show();
    }

    @OptionsItem(R.id.mnu_deleteall)
    void deleteAllClicked() {
        askSureDelete(getActivity(), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                favoritesSaver.clear();
                displayFavorites();
            }
        }, null);
    }

    @OptionsItem(R.id.mnu_shareall)
    void shareAllClicked() {
        String message = "";
        for (int i = favoritesSaver.size() - 1; i >= 0; i--) {
            message += favoritesSaver.get(i).interpret + " - " + favoritesSaver.get(i).title + "\n";
        }
        // open share dialog
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share,
                getResources().getStringArray(R.array.favorite_options_array)[2]));
    }

}

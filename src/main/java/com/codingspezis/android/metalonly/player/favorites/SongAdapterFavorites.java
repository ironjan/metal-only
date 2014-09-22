package com.codingspezis.android.metalonly.player.favorites;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.*;

import java.util.*;

/**
 * adapter for displaying a 2 row list view
 */
public class SongAdapterFavorites extends BaseAdapter {

    // hash keys
    public static final String KEY_TITLE = "MO_HK_TITLE";
    public static final String KEY_INTERPRET = "MO_HK_INTERPRET";
    public static final String KEY_THUMB = "MO_HK_THUMB";
    public static final String KEY_DATE = "MO_HK_DATE";

    private final Activity activity;
    private final ArrayList<Song> data;
    private final LayoutInflater inflater;

    public SongAdapterFavorites(Activity a, ArrayList<Song> songs) {
        activity = a;
        data = songs;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.view_list_item_song_fav, null);
        }
        Song song = data.get(position);

        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtArtist = (TextView) view.findViewById(R.id.txtArtist);

        txtTitle.setText(song.title);
        txtArtist.setText(song.interpret);

        return view;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }
}

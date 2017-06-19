package com.codingspezis.android.metalonly.player.favorites;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.core.HistoricTrack;

import java.util.ArrayList;

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
    private final ArrayList<HistoricTrack> data;
    private final LayoutInflater inflater;

    public SongAdapterFavorites(Activity a, ArrayList<HistoricTrack> songs) {
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
        HistoricTrack song = data.get(position);

        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtArtist = (TextView) view.findViewById(R.id.txtArtist);

        txtTitle.setText(song.getTitle());
        txtArtist.setText(song.getArtist());

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

    public void replaceData(ArrayList<HistoricTrack> songs) {
        this.data.clear();
        this.data.addAll(songs);
        notifyDataSetChanged();
    }
}

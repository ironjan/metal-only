package com.codingspezis.android.metalonly.player.stream;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.codingspezis.android.metalonly.player.core.HistoricTrack;
import com.codingspezis.android.metalonly.player.utils.ImageLoader;
import com.codingspezis.android.metalonly.player.views.SongHistoryView;
import com.codingspezis.android.metalonly.player.views.SongHistoryView_;

import java.util.List;

/**
 * adapter for displaying a 3 row list view
 */
public class SongAdapter extends BaseAdapter {

    private final Context context;
    private List<HistoricTrack> data;
    private final LayoutInflater inflater;
    private final ImageLoader imageLoader;

    public SongAdapter(Context context, List<HistoricTrack> d) {
        this.context = context;
        data = d;
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SongHistoryView view;
        if (convertView == null) {
            view = SongHistoryView_.build(context, null);
        } else {
            view = (SongHistoryView) convertView;
        }

        view.bind((HistoricTrack) getItem(position));
        return view;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int pos) {
        return data.get(pos);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void setSongs(List<HistoricTrack> songs) {
        this.data = songs;
    }
}

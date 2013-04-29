package com.codingspezis.android.metalonly.player.favorites;

import java.util.ArrayList;
import java.util.HashMap;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.R.*;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * SongAdapterFavorites
 * @version 08.01.2013
 * 
 * adapter for displaying a 2 row list view
 * 
 */
public class SongAdapterFavorites extends BaseAdapter{

	// hash keys
	public static final String KEY_TITLE = "MO_HK_TITLE";
	public static final String KEY_INTERPRET = "MO_HK_INTERPRET";
	public static final String KEY_THUMB = "MO_HK_THUMB";
	public static final String KEY_DATE = "MO_HK_DATE";
	
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private LayoutInflater inflater;
	
	/**
	 * constructor
	 * @param a
	 * @param d data to display
	 */
	public SongAdapterFavorites(Activity a, ArrayList<HashMap<String, String>> d){
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        if(convertView==null)
        	view = inflater.inflate(R.layout.song_fav, null);
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        ((TextView)view.findViewById(R.id.title)).setText(song.get(KEY_TITLE));
        ((TextView)view.findViewById(R.id.artist)).setText(song.get(KEY_INTERPRET));        	
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

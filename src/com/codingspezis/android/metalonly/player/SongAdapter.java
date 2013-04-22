package com.codingspezis.android.metalonly.player;

import java.util.ArrayList;
import java.util.HashMap;

import com.codingspezis.android.lazylistmodification.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ThreeRowAdapter
 * @version 06.01.2013
 * 
 * adapter for displaying a 3 row list view
 * 
 */
public class SongAdapter extends BaseAdapter{

	// hash keys
	public static final String KEY_TITLE = "MO_HK_TITLE";
	public static final String KEY_INTERPRET = "MO_HK_INTERPRET";
	public static final String KEY_THUMB = "MO_HK_THUMB";
	public static final String KEY_DATE = "MO_HK_DATE";
	
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	
	/**
	 * constructor
	 * @param a
	 * @param d data to display
	 */
	public SongAdapter(Activity a, ArrayList<HashMap<String, String>> d){
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(a.getApplicationContext());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        if(convertView==null)
        	view = inflater.inflate(R.layout.song_hist, null);
        
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        ((TextView)view.findViewById(R.id.title)).setText(song.get(KEY_TITLE));
        ((TextView)view.findViewById(R.id.artist)).setText(song.get(KEY_INTERPRET));
        try{
	        String dateParts[] = song.get(KEY_DATE).split("-");
	        ((TextView)view.findViewById(R.id.time)).setText(dateParts[0]);
	        ((TextView)view.findViewById(R.id.date)).setText(dateParts[1]);
        }catch(Exception e){
        	((TextView)view.findViewById(R.id.time)).setText("");
	        ((TextView)view.findViewById(R.id.date)).setText("");
        }
        
        ImageView image = (ImageView)view.findViewById(R.id.image);
        String thumb = song.get(KEY_THUMB);
        imageLoader.DisplayImage(thumb, image);
        	
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

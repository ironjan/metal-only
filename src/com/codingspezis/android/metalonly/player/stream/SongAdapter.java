package com.codingspezis.android.metalonly.player.stream;

import java.util.*;


import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.utils.*;

/**
 * ThreeRowAdapter
 * 
 * @version 06.01.2013
 * 
 *          adapter for displaying a 3 row list view
 * 
 */
public class SongAdapter extends BaseAdapter {

	// hash keys
	public static final String KEY_TITLE = "MO_HK_TITLE";
	public static final String KEY_INTERPRET = "MO_HK_INTERPRET";
	public static final String KEY_THUMB = "MO_HK_THUMB";
	public static final String KEY_DATE = "MO_HK_DATE";

	private final Activity activity;
	private final ArrayList<HashMap<String, String>> data;
	private final LayoutInflater inflater;
	private final ImageLoader imageLoader;

	/**
	 * constructor
	 * 
	 * @param a
	 * @param d
	 *            data to display
	 */
	public SongAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(a.getApplicationContext());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.song_hist, null);
		}

		HashMap<String, String> song = new HashMap<String, String>();
		song = data.get(position);

		final TextView textView = (TextView) view.findViewById(R.id.title);
		textView.setText(song.get(KEY_TITLE));
		((TextView) view.findViewById(R.id.artist)).setText(song
				.get(KEY_INTERPRET));
		try {
			String dateParts[] = song.get(KEY_DATE).split("-");
			((TextView) view.findViewById(R.id.time)).setText(dateParts[0]);
			((TextView) view.findViewById(R.id.date)).setText(dateParts[1]);
		} catch (Exception e) {
			((TextView) view.findViewById(R.id.time)).setText("");
			((TextView) view.findViewById(R.id.date)).setText("");
		}

		ImageView image = (ImageView) view.findViewById(R.id.list_image);
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

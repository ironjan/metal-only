package com.codingspezis.android.metalonly.player.plan;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.utils.*;

public class PlanAdapter extends BaseAdapter {

	private final ArrayList<Item> data;
	private static LayoutInflater inflater = null;
	private final ImageLoader imageLoader;

	public PlanAdapter(Context context, ArrayList<Item> data) {
		this.data = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context.getApplicationContext());
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		final Item item = data.get(position);
		if (item != null) {
			if (item.isSection()) {
				v = inflateSectionView(item);
			} else {
				v = inflateEntryItemView(position);
			}
		}
		return v;

	}

	private View inflateEntryItemView(int position) {
		View v = inflate(R.layout.view_list_row_plan);
		TextView title = (TextView) v.findViewById(R.id.txtTitle);
		TextView mod = (TextView) v.findViewById(R.id.txtMod);
		TextView time = (TextView) v.findViewById(R.id.txtTime);
		TextView genre = (TextView) v.findViewById(R.id.txtGenre);
		ImageView image = (ImageView) v.findViewById(R.id.modImage);
		ProgressBar bar = (ProgressBar) v.findViewById(R.id.progress);
		PlanData tmpData = data.get(position).getPlanData();

		title.setText(tmpData.getTitle());
		mod.setText(tmpData.getMod());
		time.setText(tmpData.getTimeString());
		genre.setText(tmpData.getGenre());
		imageLoader.DisplayImage(tmpData.getMod(), image);

		// workaround for bottom margin bug
		bar.setProgress(100 - tmpData.getProgress());
		return v;
	}

	private View inflateSectionView(final Item item) {
		View v = inflate(R.drawable.plan_section);
		v.setOnClickListener(null);
		v.setOnLongClickListener(null);
		v.setLongClickable(false);
		final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);

		SectionItem si = (SectionItem) item;
		sectionView.setText(si.getTitle());
		return v;
	}

	private View inflate(int layout) {
		return inflater.inflate(layout, null);
	}

}
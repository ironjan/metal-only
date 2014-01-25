package com.codingspezis.android.metalonly.player.plan;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.plan.views.*;
import com.codingspezis.android.metalonly.player.utils.*;

public class PlanAdapter extends BaseAdapter {

	private final ArrayList<Item> data;
	@SuppressWarnings("unused")
	private static LayoutInflater inflater = null;
	@SuppressWarnings("unused")
	private final ImageLoader imageLoader;
	private Context context;

	public PlanAdapter(Context context, ArrayList<Item> data) {
		this.context = context;

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
				v = inflateSectionView((SectionItem) item);
			} else {
				v = inflateEntryItemView(position);
			}
		}
		return v;

	}

	private View inflateEntryItemView(int position) {
		PlanEntryView view = PlanEntryView_.build(context, null);
		PlanData tmpData = data.get(position).getPlanData();
		view.bind(tmpData);
		return view;
	}

	private View inflateSectionView(final SectionItem item) {
		SectionView view = SectionView_.build(context, null);
		view.setOnClickListener(null);
		view.setOnLongClickListener(null);
		view.setLongClickable(false);
		view.bind(item);
		return view;
	}

}
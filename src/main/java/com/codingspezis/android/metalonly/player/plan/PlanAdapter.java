package com.codingspezis.android.metalonly.player.plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.codingspezis.android.metalonly.player.plan.views.PlanEntryView;
import com.codingspezis.android.metalonly.player.plan.views.PlanEntryView_;
import com.codingspezis.android.metalonly.player.plan.views.SectionView;
import com.codingspezis.android.metalonly.player.plan.views.SectionView_;
import com.codingspezis.android.metalonly.player.utils.ImageLoader;

import java.util.ArrayList;

public class PlanAdapter extends BaseAdapter {

    @SuppressWarnings("unused")
    private static LayoutInflater inflater = null;
    private final ArrayList<PlanItem> data;
    @SuppressWarnings("unused")
    private final ImageLoader imageLoader;
    private Context context;

    public PlanAdapter(Context context, ArrayList<PlanItem> data) {
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

        final PlanItem item = data.get(position);
        if (item != null) {
            if (item.isSection()) {
                v = inflateSectionView((PlanSectionItem) item);
            } else {
                v = inflateEntryItemView(position);
            }
        }
        return v;

    }

    private View inflateEntryItemView(int position) {
        PlanEntryView view = PlanEntryView_.build(context, null);
        ShowInformation tmpData = data.get(position).getPlanData();
        view.bind(tmpData);
        return view;
    }

    private View inflateSectionView(final PlanSectionItem item) {
        SectionView view = SectionView_.build(context, null);
        view.setOnClickListener(null);
        view.setOnLongClickListener(null);
        view.setLongClickable(false);
        view.bind(item);
        return view;
    }

}
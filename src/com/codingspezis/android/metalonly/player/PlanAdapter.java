package com.codingspezis.android.metalonly.player;

import java.util.ArrayList;

import com.codingspezis.android.lazylistmodification.ImageLoader;
import com.codingspezis.android.metalonly.player.PlanActivity.Item;
import com.codingspezis.android.metalonly.player.PlanActivity.PlanData;
import com.codingspezis.android.metalonly.player.PlanActivity.SectionItem;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class PlanAdapter extends BaseAdapter {
	 
    private Activity activity;
    private ArrayList<Item> data;
    private static LayoutInflater inflater=null;
    private ImageLoader imageLoader;
 
    public PlanAdapter(Activity a, ArrayList<Item> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(a.getApplicationContext());
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
     
        final Item i = data.get(position);
        if (i != null) {
            if(i.isSection()){
                SectionItem si = (SectionItem)i;
                v = inflater.inflate(R.drawable.plan_section, null);
                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);
                final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                sectionView.setText(si.getTitle());
            }else{
                v = inflater.inflate(R.layout.plan_list_row, null);
                TextView title = (TextView)v.findViewById(R.id.title); 
                TextView mod = (TextView)v.findViewById(R.id.mod);
                TextView time = (TextView)v.findViewById(R.id.time); 
                TextView date = (TextView)v.findViewById(R.id.date); 
                TextView genre = (TextView)v.findViewById(R.id.genre); 
                ImageView image = (ImageView)v.findViewById(R.id.list_image);
                ProgressBar bar = (ProgressBar)v.findViewById(R.id.progress);
                PlanData tmpData = data.get(position).getPlanData(); 
                
                // Setting all values in listview
                title.setText(tmpData.getTitle());
                mod.setText(tmpData.getMod());
                time.setText(tmpData.getTimeString());
                date.setText(tmpData.getDateString());
                genre.setText(tmpData.getGenre());
                imageLoader.DisplayImage(tmpData.getMod(), image);
                bar.setProgress(tmpData.getProgress());
            }
        }
        return v;
        
        
    }

    
}
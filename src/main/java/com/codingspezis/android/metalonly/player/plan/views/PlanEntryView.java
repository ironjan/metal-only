package com.codingspezis.android.metalonly.player.plan.views;

import android.content.*;
import android.util.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.plan.*;
import com.codingspezis.android.metalonly.player.utils.*;

import org.androidannotations.annotations.*;

/**
 * Custom view to display PlanData
 */
@EViewGroup(R.layout.view_list_row_plan)
public class PlanEntryView extends RelativeLayout implements CustomDataView<PlanData> {

    @ViewById
    TextView txtTitle, txtMod, txtTime, txtGenre;
    @ViewById
    ImageView modImage;
    @ViewById
    ProgressBar progress;
    private ImageLoader imageLoader;

    public PlanEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @Override
    public void bind(PlanData planData) {
        txtTitle.setText(planData.getTitle());
        txtMod.setText(planData.getMod());
        txtTime.setText(planData.getTimeString());
        txtGenre.setText(planData.getGenre());
        imageLoader.DisplayImage(planData.getMod(), modImage);
        progress.setProgress(100 - planData.getProgress());
    }
}

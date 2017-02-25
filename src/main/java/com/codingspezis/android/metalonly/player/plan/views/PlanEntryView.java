package com.codingspezis.android.metalonly.player.plan.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.plan.PlanData;
import com.codingspezis.android.metalonly.player.utils.ImageLoader;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

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

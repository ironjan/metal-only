package com.codingspezis.android.metalonly.player.plan.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.plan.PlanEntryAndDataUnification;
import com.codingspezis.android.metalonly.player.utils.ImageLoader;
import com.codingspezis.android.metalonly.player.plan.PlanEntryDateHelper;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Custom view to display PlanData
 */
@EViewGroup(R.layout.view_list_row_plan)
public class PlanEntryView extends RelativeLayout implements CustomDataView<PlanEntryAndDataUnification> {

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
    public void bind(PlanEntryAndDataUnification planData) {
        txtTitle.setText(planData.showTitle());
        txtMod.setText(planData.moderator());
        txtTime.setText(PlanEntryDateHelper.fullTimeString(planData));
        txtGenre.setText(planData.genre());
        imageLoader.DisplayImage(planData.moderator(), modImage);
        progress.setProgress(100 - computeShowProgress(planData));
    }

    private int computeShowProgress(PlanEntryAndDataUnification planData) {
        Calendar cal = new GregorianCalendar();
        float timeLeftInMillis = planData.end().getTime() - cal.getTimeInMillis();
        float totalDurationInMillis = planData.end().getTime() - planData.start().getTime();

        return (int) ((timeLeftInMillis / totalDurationInMillis) * 100);
    }
}

package com.codingspezis.android.metalonly.player.plan.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.plan.ShowInformation;
import com.codingspezis.android.metalonly.player.utils.ImageLoader;
import com.codingspezis.android.metalonly.player.plan.PlanEntryDateHelper;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Custom view to display {@link ShowInformation}
 */
@EViewGroup(R.layout.view_list_row_plan)
public class PlanEntryView extends RelativeLayout implements CustomDataView<ShowInformation> {

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
    public void bind(ShowInformation planData) {
        txtTitle.setText(planData.getShowTitle());
        txtMod.setText(planData.getModerator());
        txtTime.setText(PlanEntryDateHelper.fullTimeString(planData));
        txtGenre.setText(planData.getGenre());
        imageLoader.DisplayImage(planData.getModerator(), modImage);
        progress.setProgress(100 - computeShowProgress(planData));
    }

    private int computeShowProgress(ShowInformation planData) {
        Calendar cal = new GregorianCalendar();
        float timeLeftInMillis = planData.getEndDate().getTime() - cal.getTimeInMillis();
        float totalDurationInMillis = planData.getEndDate().getTime() - planData.getStartDate().getTime();

        return (int) ((timeLeftInMillis / totalDurationInMillis) * 100);
    }
}

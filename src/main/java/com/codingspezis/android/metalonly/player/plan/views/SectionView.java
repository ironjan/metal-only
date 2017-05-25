package com.codingspezis.android.metalonly.player.plan.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.plan.PlanSectionItem;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * A view to divide sections in plans.
 */
@EViewGroup(R.layout.view_plan_section)
public class SectionView extends LinearLayout implements CustomDataView<PlanSectionItem> {

    @ViewById(android.R.id.title)
    TextView textTitle;

    public SectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bind(PlanSectionItem sectionItem) {
        textTitle.setText(sectionItem.getTitle());
    }
}

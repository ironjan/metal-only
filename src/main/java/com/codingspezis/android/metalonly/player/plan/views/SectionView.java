package com.codingspezis.android.metalonly.player.plan.views;

import android.content.*;
import android.util.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.plan.*;

import org.androidannotations.annotations.*;

/**
 * A view to divide sections in plans.
 */
@EViewGroup(R.layout.view_plan_section)
public class SectionView extends LinearLayout {

    @ViewById(android.R.id.title)
    TextView textTitle;

    public SectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bind(SectionItem sectionItem) {
        textTitle.setText(sectionItem.getTitle());
    }
}

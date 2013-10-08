package com.codingspezis.android.metalonly.player.plan.views;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.plan.*;
import com.googlecode.androidannotations.annotations.*;

import android.content.*;
import android.util.*;
import android.widget.*;

@EViewGroup(R.layout.view_plan_section)
public class SectionView extends LinearLayout {

	@ViewById
	TextView title;
	
	public SectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void bind(SectionItem sectionItem ){
		String title2 = sectionItem.getTitle();
		title.setText(title2);
	}
}

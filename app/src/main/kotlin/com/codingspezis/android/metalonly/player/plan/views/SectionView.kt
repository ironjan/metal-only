package com.codingspezis.android.metalonly.player.plan.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView

import com.codingspezis.android.metalonly.player.R
import com.codingspezis.android.metalonly.player.plan.PlanSectionItem

import org.androidannotations.annotations.EViewGroup
import org.androidannotations.annotations.ViewById

/**
 * A view to divide sections in plans.
 */
@EViewGroup(R.layout.view_plan_section)
open class SectionView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs), CustomDataView<PlanSectionItem> {

    @JvmField
    @ViewById(android.R.id.title)
    protected var textTitle: TextView? = null

    override fun bind(t: PlanSectionItem) {
        textTitle?.text = t.title
    }
}

package com.codingspezis.android.metalonly.player.plan.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.codingspezis.android.metalonly.player.R
import com.codingspezis.android.metalonly.player.plan.ShowInformation
import com.codingspezis.android.metalonly.player.utils.ImageLoader
import com.codingspezis.android.metalonly.player.plan.ShowInformationDateHelper

import org.androidannotations.annotations.EViewGroup
import org.androidannotations.annotations.ViewById

import java.util.GregorianCalendar

/**
 * Custom view to display [ShowInformation]
 */
@EViewGroup(R.layout.view_list_row_plan)
open class PlanEntryView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs), CustomDataView<ShowInformation> {

    @JvmField
    @ViewById
    protected var txtTitle: TextView? = null
    @JvmField
    @ViewById
    protected var txtMod: TextView? = null
    @JvmField
    @ViewById
    protected var txtTime: TextView? = null
    @JvmField
    @ViewById
    protected var txtGenre: TextView? = null
    @JvmField
    @ViewById
    protected var modImage: ImageView? = null
    @JvmField
    @ViewById
    protected var progress: ProgressBar? = null

    private val imageLoader: ImageLoader = ImageLoader(context.applicationContext)

    override fun bind(t: ShowInformation) {
        txtTitle?.text = t.showTitle
        txtMod?.text = t.moderator
        txtTime?.text = ShowInformationDateHelper.fullTimeString(t)
        txtGenre?.text = t.genre
        imageLoader.loadImage(t.moderator, modImage!!)
        progress?.progress = 100 - computeShowProgress(t)
    }

    private fun computeShowProgress(planData: ShowInformation): Int {
        val cal = GregorianCalendar()
        val timeLeftInMillis = (planData.endDate.time - cal.timeInMillis).toFloat()
        val totalDurationInMillis = (planData.endDate.time - planData.startDate.time).toFloat()

        return (timeLeftInMillis / totalDurationInMillis * 100).toInt()
    }
}

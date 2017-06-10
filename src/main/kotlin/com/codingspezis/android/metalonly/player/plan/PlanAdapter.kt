package com.codingspezis.android.metalonly.player.plan

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.codingspezis.android.metalonly.player.plan.views.PlanEntryView_
import com.codingspezis.android.metalonly.player.plan.views.SectionView_

class PlanAdapter(private val context: Context, private val data: ArrayList<PlanItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = data[position]
        if (item.isSection) {
            return inflateSectionView(item as PlanSectionItem)
        } else {
            return inflateEntryItemView(position)
        }
    }

    private fun inflateEntryItemView(position: Int): View {
        val view = PlanEntryView_.build(context, null)
        val tmpData = (data[position] as PlanRealEntryItem).showInformation
        view.bind(tmpData!!)
        return view
    }

    private fun inflateSectionView(item: PlanSectionItem): View {
        val view = SectionView_.build(context, null)
        view.bind(item)
        return view
    }

}
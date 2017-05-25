package com.codingspezis.android.metalonly.player.plan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.codingspezis.android.metalonly.player.plan.views.PlanEntryView_
import com.codingspezis.android.metalonly.player.plan.views.SectionView_
import com.codingspezis.android.metalonly.player.utils.ImageLoader
import java.util.*

class PlanAdapter(private val context: Context, private val data: ArrayList<PlanItem>) : BaseAdapter() {
    private val imageLoader: ImageLoader

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        imageLoader = ImageLoader(context.applicationContext)
    }

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
        view.setOnClickListener(null)
        view.setOnLongClickListener(null)
        view.isLongClickable = false
        view.bind(item)
        return view
    }

    companion object {
        private var inflater: LayoutInflater? = null
    }

}
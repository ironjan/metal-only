package com.codingspezis.android.metalonly.player.plan

import com.codingspezis.android.metalonly.player.core.ShowInformation
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.res.StringArrayRes
import org.androidannotations.annotations.res.StringRes

import java.util.ArrayList
import java.util.Calendar

@EBean
open class PlanEntryToItemConverter {

    @JvmField
    @StringRes
    protected var plan: String? = null

    @JvmField
    @StringArrayRes
    protected var days: Array<String>? = null

    internal var todayStartIndex: Int = 0

    fun convertToPlan(listEvents: ArrayList<ShowInformation>): ArrayList<PlanItem> {
        val listItems = ArrayList<PlanItem>()

        var currentDayIndex = -1

        for (i in listEvents.indices) {
            val currentShow = listEvents[i]
            val currentShowDayIndex = getDayIndex(currentShow)
            if (currentShowDayIndex != currentDayIndex) {
                listItems.add(PlanSectionItem(days!![currentShowDayIndex]))
                currentDayIndex++
                if (isToday(currentDayIndex)) {
                    todayStartIndex = listItems.size - 1
                }
            }
            listItems.add(PlanRealEntryItem(currentShow))
        }
        return listItems
    }

    private fun isToday(dayIndex: Int): Boolean {
        val cal = Calendar.getInstance()
        return getGermanDayOfWeek(cal) == dayIndex
    }

    private fun getDayIndex(d: ShowInformation): Int {
        val cal = Calendar.getInstance()
        cal.time = d.startDate
        return getGermanDayOfWeek(cal)
    }

    private fun getGermanDayOfWeek(cal: Calendar) = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7

    fun todayStartIndex(): Int {
        return todayStartIndex
    }
}
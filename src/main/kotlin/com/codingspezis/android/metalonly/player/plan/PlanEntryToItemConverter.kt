package com.codingspezis.android.metalonly.player.plan

import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.res.StringArrayRes
import org.androidannotations.annotations.res.StringRes

import java.util.ArrayList
import java.util.Calendar
import java.util.GregorianCalendar

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
        // TODO refactor this method
        val listItems = ArrayList<PlanItem>()

        var dayIndex = 0

        listItems.add(PlanSectionItem(days!![dayIndex]))

        for (i in listEvents.indices) {
            val currentShow = listEvents[i]
            listItems.add(PlanRealEntryItem(currentShow))

            if (hasNextListItem(listEvents, i)) {
                val nextShow = listEvents[i + 1]
                val notOnSameDay = getDayIndex(currentShow) != getDayIndex(nextShow)
                if (notOnSameDay) {
                    dayIndex++
                    listItems.add(PlanSectionItem(days!![dayIndex]))
                    if (isToday(dayIndex)) {
                        todayStartIndex = listItems.size - 1
                    }
                }
            }
        }
        return listItems
    }

    private fun isToday(dayIndex: Int): Boolean {
        val cal = GregorianCalendar()
        return (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 == dayIndex
    }

    private fun getDayIndex(d: ShowInformation): Int {
        val cal = Calendar.getInstance()
        cal.time = d.startDate
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    private fun hasNextListItem(listEvents: ArrayList<ShowInformation>, i: Int): Boolean {
        return i < listEvents.size - 1
    }

    fun todayStartIndex(): Int {
        return todayStartIndex
    }
}
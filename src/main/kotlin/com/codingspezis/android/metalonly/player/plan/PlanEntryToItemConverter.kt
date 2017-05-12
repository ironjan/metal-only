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

    fun convertToPlan(listEvents: ArrayList<PlanData>): ArrayList<PlanItem> {
        // TODO refactor this method
        val listItems = ArrayList<PlanItem>()
        val cal = GregorianCalendar()

        var day = 0

        var nextDaySection = PlanSectionItem(days!![day])

        listItems.add(nextDaySection)

        for (i in listEvents.indices) {
            val d = listEvents[i]
            listItems.add(PlanRealEntryItem(d))
            if (hasNextListItem(listEvents, i)) {
                val nextItem = listEvents[i + 1]
                if (notOnSameDay(d, nextItem)) {
                    day++
                    nextDaySection = PlanSectionItem(days!![day])
                    listItems.add(nextDaySection)
                    if (isToday(day)) {
                        // TODO setTodayListStartIndex(listItems.size());
                    }
                    val dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                    if (day == dayOfWeek) {
                        val pos = listItems.size - 1
                        // TODO planFragment.getList().setSelection(pos);
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

    private fun notOnSameDay(d: PlanData, nextItem: PlanData): Boolean {
        return !d.sameDay(nextItem)
    }

    private fun hasNextListItem(listEvents: ArrayList<PlanData>, i: Int): Boolean {
        return i < listEvents.size - 1
    }
}
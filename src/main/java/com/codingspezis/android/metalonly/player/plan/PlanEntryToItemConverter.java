package com.codingspezis.android.metalonly.player.plan;

import com.codingspezis.android.metalonly.player.fragments.PlanFragment;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

@EBean
public class PlanEntryToItemConverter {

    @StringRes
    String plan;
    @StringArrayRes
    String[] days;


    public ArrayList<PlanItem> convertToPlan(ArrayList<PlanData> listEvents) {
        // TODO refactor this method
        ArrayList<PlanItem> listItems = new ArrayList<PlanItem>();
        Calendar cal = new GregorianCalendar();

        int day = 0;

        PlanSectionItem nextDaySection = new PlanSectionItem(days[day]);

        listItems.add(nextDaySection);

        for (int i = 0; i < listEvents.size(); i++) {
            PlanData d = listEvents.get(i);
            listItems.add(new PlanRealEntryItem(d));
            if (hasNextListItem(listEvents, i)) {
                PlanData nextItem = listEvents.get(i + 1);
                if (notOnSameDay(d, nextItem)) {
                    day++;
                    nextDaySection = new PlanSectionItem(days[day]);
                    listItems.add(nextDaySection);
                    if (isToday(day)){
                        // TODO setTodayListStartIndex(listItems.size());
                    }
                    int dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
                    if (day == dayOfWeek) {
                        final int pos = listItems.size() - 1;
                        // TODO planFragment.getList().setSelection(pos);
                    }
                }
            }
        }
        return listItems;
    }


    private boolean isToday(int dayIndex) {
        Calendar cal = new GregorianCalendar();
        return (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 == dayIndex;
    }

    private boolean notOnSameDay(PlanData d, PlanData nextItem) {
        return !d.sameDay(nextItem);
    }

    private boolean hasNextListItem(ArrayList<PlanData> listEvents, int i) {
        return i < listEvents.size() - 1;
    }
}
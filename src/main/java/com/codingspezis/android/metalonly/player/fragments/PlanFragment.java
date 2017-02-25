package com.codingspezis.android.metalonly.player.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.support.v4.app.ListFragment;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.plan.EntryItem;
import com.codingspezis.android.metalonly.player.plan.Item;
import com.codingspezis.android.metalonly.player.plan.PlanAdapter;
import com.codingspezis.android.metalonly.player.plan.PlanData;
import com.codingspezis.android.metalonly.player.plan.PlanEntryClickListener;
import com.codingspezis.android.metalonly.player.plan.SectionItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;


@EFragment(R.layout.fragment_plan)
@SuppressLint({"SimpleDateFormat", "Registered"})
public class PlanFragment extends ListFragment {

    public static final SimpleDateFormat DATE_FORMAT_PARSER = new SimpleDateFormat(
            "{dd.MM.yy HH:mm");
    private static final String pattern = "(.*?)_(.*?)_(.*)_(.*)_(.*)";
    @StringRes
    String plan;
    @StringArrayRes
    String[] days;
    private int todayListStartIndex;


    public static PlanFragment newInstance(String site) {
        return PlanFragment_.builder()
                .arg(PlanActivity.KEY_SITE, site)
                .build();
    }


    @AfterViews
    void afterViews() {
        String site = getArguments().getString(PlanActivity.KEY_SITE);
        ArrayList<PlanData> listEvents = extractEvents(site);
        ArrayList<Item> listItems = convertToPlan(listEvents);
        PlanAdapter adapter = new PlanAdapter(getActivity(), listItems);
        getListView().setAdapter(adapter);
        setSelection(todayListStartIndex);
    }

    private ArrayList<Item> convertToPlan(ArrayList<PlanData> listEvents) {
        // TODO refactor this method
        ArrayList<Item> listItems = new ArrayList<Item>();
        Calendar cal = new GregorianCalendar();

        int day = 0;

        SectionItem nextDaySection = new SectionItem(days[day]);

        listItems.add(nextDaySection);

        for (int i = 0; i < listEvents.size(); i++) {
            PlanData d = listEvents.get(i);
            listItems.add(new EntryItem(d));
            if (hasNextListItem(listEvents, i)) {
                PlanData nextItem = listEvents.get(i + 1);
                if (notOnSameDay(d, nextItem)) {
                    day++;
                    if (isToday(day))
                        todayListStartIndex = listItems.size();
                    nextDaySection = new SectionItem(days[day]);
                    listItems.add(nextDaySection);
                    int dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
                    if (day == dayOfWeek) {
                        final int pos = listItems.size() - 1;
                        getListView().setSelection(pos);
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

    private ArrayList<PlanData> extractEvents(String site) {
        StringTokenizer tokenizer = new StringTokenizer(site, "}");

        ArrayList<PlanData> listEvents = new ArrayList<PlanData>();

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            PlanData planData = convertTokenToPlanEntry(token);
            if (null != planData) {
                listEvents.add(planData);
            }
        }
        return listEvents;
    }

    private PlanData convertTokenToPlanEntry(String token) {
        try {
            if (hasModerator(token)) {
                GregorianCalendar tmpCal = new GregorianCalendar();
                tmpCal.setTimeInMillis(DATE_FORMAT_PARSER.parse(token.replaceAll(pattern, "$1"))
                        .getTime());

                PlanData planData = new PlanData(token.replaceAll(pattern, "$3"), token.replaceAll(
                        pattern, "$4"), token.replaceAll(pattern, "$5"));
                planData.setStart(tmpCal);
                planData.setDuration(Integer.parseInt(token.replaceAll(pattern, "$2")));
                return planData;
            }
        } catch (ParseException e) {
            // drop entry with wrongly formatted date
        }

        return null;
    }

    private boolean hasModerator(String token) {
        boolean metalHeadIsMod = token.replaceAll(pattern, "$3").equals("MetalHead");
        boolean hasNoMod = token.replaceAll(pattern, "$3").equals("frei");
        boolean hasModerator = !(metalHeadIsMod || hasNoMod);
        return hasModerator;
    }

    @ItemClick(android.R.id.list)
    void entryClicked(Object clickedObject) {
        try {
            EntryItem entryItem = (EntryItem) clickedObject;
            PlanData planData = entryItem.getPlanData();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.plan_options_array, new PlanEntryClickListener(planData, getActivity()));
            builder.show();
        } catch (ClassCastException e) {
            // don't need to do stuff
        }
    }


}

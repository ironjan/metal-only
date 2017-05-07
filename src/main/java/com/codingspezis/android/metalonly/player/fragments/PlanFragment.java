package com.codingspezis.android.metalonly.player.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.codingspezis.android.metalonly.player.BuildConfig;
import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.plan.EntryItem;
import com.codingspezis.android.metalonly.player.plan.Item;
import com.codingspezis.android.metalonly.player.plan.PlanAdapter;
import com.codingspezis.android.metalonly.player.plan.PlanData;
import com.codingspezis.android.metalonly.player.plan.PlanEntryClickListener;
import com.codingspezis.android.metalonly.player.plan.SectionItem;
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPI;
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPIWrapper;
import com.codingspezis.android.metalonly.player.utils.jsonapi.Plan;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;


@EFragment(R.layout.fragment_plan)
@SuppressLint({"SimpleDateFormat", "Registered"})
public class PlanFragment extends Fragment {

    public static final SimpleDateFormat DATE_FORMAT_PARSER = new SimpleDateFormat(
            "{dd.MM.yy HH:mm");
    private static final String pattern = "(.*?)_(.*?)_(.*)_(.*)_(.*)";
    @StringRes
    String plan;
    @StringArrayRes
    String[] days;
    private int todayListStartIndex;

    @FragmentArg
    String site;

    @ViewById(android.R.id.list)
    ListView list;

    @ViewById(android.R.id.empty)
    View empty;

    @RestService
    MetalOnlyAPI api;

    @Bean
    MetalOnlyAPIWrapper apiWrapper;

    public static PlanFragment newInstance(String site) {
        return PlanFragment_.builder()
                .site(site)
                .build();
    }

    @AfterInject
    @Background
    void loadPlan() {
        if (!BuildConfig.DEBUG) {
            // Currently, we only want to test this in debug
            return;
        }

        if (!apiWrapper.hasConnection()) {
            showToast(R.string.no_internet);
            return;
        }

        try {
            Plan plan = api.getPlan();
            planLoaded(plan);
        } catch (RestClientException e) {
            // TODO Can we catch ResourceAccessException to HttpStatusCodeException show better info?
            showToast(R.string.plan_failed_to_load);
        }
    }

    @UiThread
    void planLoaded(Plan plan) {
            Toast.makeText(getActivity(), "Plan loaded...", Toast.LENGTH_LONG).show();
    }

    @UiThread
    void showToast(int stringId) {
        Toast.makeText(getActivity(), stringId, Toast.LENGTH_LONG).show();
    }

    @AfterViews
    void afterViews() {
        ArrayList<PlanData> listEvents = extractEvents(site);
        ArrayList<Item> listItems = convertToPlan(listEvents);
        PlanAdapter adapter = new PlanAdapter(getActivity(), listItems);

        list.setEmptyView(empty);
        list.setAdapter(adapter);
        list.setSelection(todayListStartIndex);
    }

    private ArrayList<Item> convertToPlan(ArrayList<PlanData> listEvents) {
        // TODO refactor this method
        ArrayList<Item> listItems = new ArrayList<>();
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
                    nextDaySection = new SectionItem(days[day]);
                    listItems.add(nextDaySection);
                    if (isToday(day))
                        todayListStartIndex = listItems.size();
                    int dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
                    if (day == dayOfWeek) {
                        final int pos = listItems.size() - 1;
                        list.setSelection(pos);
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

        ArrayList<PlanData> listEvents = new ArrayList<>();

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
        return !(metalHeadIsMod || hasNoMod);
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

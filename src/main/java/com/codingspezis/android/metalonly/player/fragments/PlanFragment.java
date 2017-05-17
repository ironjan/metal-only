package com.codingspezis.android.metalonly.player.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.plan.PlanAdapter;
import com.codingspezis.android.metalonly.player.plan.ShowInformation;
import com.codingspezis.android.metalonly.player.plan.PlanEntryClickListener;
import com.codingspezis.android.metalonly.player.plan.PlanEntryToItemConverter;
import com.codingspezis.android.metalonly.player.plan.PlanItem;
import com.codingspezis.android.metalonly.player.plan.PlanRealEntryItem;
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPI;
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPIWrapper;
import com.codingspezis.android.metalonly.player.utils.jsonapi.Plan;

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
import org.springframework.web.client.RestClientException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

@EFragment(R.layout.fragment_plan)
@SuppressLint({"SimpleDateFormat", "Registered"})
public class PlanFragment extends Fragment {

    @Bean
    PlanEntryToItemConverter planEntryToItemConverter;

    @StringRes
    String plan;
    @StringArrayRes
    String[] days;

    @FragmentArg
    String site;

    @ViewById(android.R.id.list)
    ListView list;

    @ViewById(android.R.id.empty)
    View empty;

    @ViewById
    TextView loadingMessageTextView;

    @ViewById(android.R.id.progress)
    ProgressBar loadingProgressBar;

    @RestService
    MetalOnlyAPI api;

    @Bean
    MetalOnlyAPIWrapper apiWrapper;

    public static PlanFragment newInstance(String site) {
        return PlanFragment_.builder()
                .site(site)
                .build();
    }

    @AfterViews
    void bindEmptyViewToList(){
        list.setEmptyView(empty);
    }

    @AfterViews
    @Background
    void loadPlan() {
        if (!apiWrapper.hasConnection()) {
            updateEmptyViewOnFailure(R.string.no_internet);
            return;
        }

        try {
            Plan plan = api.getPlan();
            planLoaded(plan);
        } catch (RestClientException e) {
            // TODO Can we catch ResourceAccessException to HttpStatusCodeException show better info?
            updateEmptyViewOnFailure(R.string.plan_failed_to_load);
        }
    }

    @UiThread
    void planLoaded(Plan plan) {
        ArrayList<ShowInformation> shows = new ArrayList<>();
        Collections.addAll(shows, plan.getPlan());

        ArrayList<PlanItem> listItems = planEntryToItemConverter.convertToPlan(shows);
        PlanAdapter adapter = new PlanAdapter(getActivity(), listItems);

        list.setAdapter(adapter);
        list.setSelection(planEntryToItemConverter.todayStartIndex());
    }

    @UiThread
    void updateEmptyViewOnFailure(int stringResourceId) {
        loadingMessageTextView.setText(stringResourceId);
        loadingProgressBar.setVisibility(View.GONE);
    }

    @ItemClick(android.R.id.list)
    void entryClicked(Object clickedObject) {
        try {
            PlanRealEntryItem entryItem = (PlanRealEntryItem) clickedObject;
            ShowInformation planData = entryItem.getPlanData();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.plan_options_array, new PlanEntryClickListener(planData, getActivity()));
            builder.show();
        } catch (ClassCastException e) {
            // don't need to do stuff
        }
    }
}

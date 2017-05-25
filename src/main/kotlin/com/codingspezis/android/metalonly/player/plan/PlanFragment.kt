package com.codingspezis.android.metalonly.player.plan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView

import com.codingspezis.android.metalonly.player.R
import com.codingspezis.android.metalonly.player.plan.PlanAdapter
import com.codingspezis.android.metalonly.player.plan.ShowInformation
import com.codingspezis.android.metalonly.player.plan.PlanEntryClickListener
import com.codingspezis.android.metalonly.player.plan.PlanEntryToItemConverter
import com.codingspezis.android.metalonly.player.plan.PlanItem
import com.codingspezis.android.metalonly.player.plan.PlanRealEntryItem
import com.codingspezis.android.metalonly.player.utils.jsonapi.*

import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Background
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.androidannotations.annotations.ItemClick
import org.androidannotations.annotations.UiThread
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.res.StringArrayRes
import org.androidannotations.annotations.res.StringRes
import org.androidannotations.rest.spring.annotations.RestService
import org.springframework.web.client.RestClientException

import java.util.ArrayList
import java.util.Collections

@EFragment(R.layout.fragment_plan)
@SuppressLint("SimpleDateFormat", "Registered")
open class PlanFragment : Fragment() {

    @JvmField
    @Bean
    internal var planEntryToItemConverter: PlanEntryToItemConverter? = null

    @JvmField
    @FragmentArg
    internal var site: String? = null

    @JvmField
    @ViewById(android.R.id.list)
    internal var list: ListView? = null

    @JvmField
    @ViewById(android.R.id.empty)
    internal var empty: View? = null

    @JvmField
    @ViewById
    internal var loadingMessageTextView: TextView? = null

    @JvmField
    @ViewById(android.R.id.progress)
    internal var loadingProgressBar: ProgressBar? = null

    @JvmField
    @RestService
    internal var api: MetalOnlyAPI? = null

    @JvmField
    @Bean
    internal var apiWrapper: MetalOnlyAPIWrapper? = null

    /** @todo use better default value */
    @JvmField
    @StringRes
    internal var plan_failed_to_load: String = ""
    /** @todo use better default value */
    @JvmField
    @StringRes
    internal var no_internet: String = ""
    @JvmField
    @StringRes
    internal var plan: String? = null
    @JvmField
    @StringArrayRes
    internal var days: Array<String>? = null

    @AfterViews
    internal fun bindEmptyViewToList() {
        list!!.emptyView = empty
    }

    @AfterViews
    @Background
    internal open fun loadPlan() {
        if (!apiWrapper!!.hasConnection()) {
            updateEmptyViewOnFailure(no_internet)
            return
        }

        try {
            apiResponseReceived(api!!.plan)
        } catch (e: NoInternetException) {
            updateEmptyViewOnFailure(no_internet)
        } catch (e: RestClientException) {
            // TODO Can we catch ResourceAccessException to HttpStatusCodeException show better info?
            val text = plan_failed_to_load + ":\n" + e.message
            updateEmptyViewOnFailure(text)
        }

    }

    @UiThread
    internal open fun apiResponseReceived(plan: Plan?) {
        // TODO check, if we're still visible?
        if (plan == null) {
            updateEmptyViewOnFailure(plan_failed_to_load)
            return
        }

        val shows = ArrayList<ShowInformation>()
        Collections.addAll<PlanEntry>(shows, *plan.plan)

        val listItems = planEntryToItemConverter!!.convertToPlan(shows)
        val adapter = PlanAdapter(activity, listItems)

        list!!.adapter = adapter
        list!!.setSelection(planEntryToItemConverter!!.todayStartIndex())
    }

    @UiThread
    internal open fun updateEmptyViewOnFailure(text: String) {
        loadingMessageTextView!!.text = text
        loadingProgressBar!!.visibility = View.GONE
    }

    @ItemClick(android.R.id.list)
    internal fun entryClicked(clickedObject: Any) {
        try {
            val entryItem = clickedObject as PlanRealEntryItem
            val builder = AlertDialog.Builder(activity)
            builder.setItems(R.array.plan_options_array, PlanEntryClickListener(entryItem.showInformation!!, activity))
            builder.show()
        } catch (e: ClassCastException) {
            // don't need to do stuff
        }

    }
}

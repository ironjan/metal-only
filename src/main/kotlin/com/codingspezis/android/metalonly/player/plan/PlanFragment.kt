package com.codingspezis.android.metalonly.player.plan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.codingspezis.android.metalonly.player.R
import com.codingspezis.android.metalonly.player.utils.jsonapi.*
import org.androidannotations.annotations.*
import org.androidannotations.annotations.res.StringArrayRes
import org.androidannotations.annotations.res.StringRes
import org.androidannotations.rest.spring.annotations.RestService
import org.springframework.web.client.RestClientException
import java.util.*

@EFragment(R.layout.fragment_plan)
@SuppressLint("SimpleDateFormat", "Registered")
open class PlanFragment : Fragment() {

    @JvmField
    @FragmentArg
    internal var site: String? = null

    @JvmField
    @ViewById(android.R.id.list)
    internal var list: ListView? = null

    @JvmField
    @ViewById
    internal var loadingMessageTextView: TextView? = null
    @JvmField
    @ViewById(android.R.id.progress)
    internal var loadingProgressBar: ProgressBar? = null
    @JvmField
    @StringRes
    internal var plan_failed_to_load: String? = null
    @JvmField
    @ViewById(android.R.id.empty)
    internal var empty: View? = null

    @JvmField
    @RestService
    internal var api: MetalOnlyAPI? = null

    @JvmField
    @Bean
    internal var planEntryToItemConverter: PlanEntryToItemConverter? = null
    @JvmField
    @Bean
    internal var apiWrapper: MetalOnlyAPIWrapper? = null

    @StringRes
    internal var plan: String? = null
    @JvmField
    @StringArrayRes
    internal var days: Array<String>? = null
    @JvmField
    @StringRes
    internal var no_internet: String? = null

    @AfterViews
    internal fun bindEmptyViewToList() {
        list!!.emptyView = empty
    }

    @AfterViews
    @Background
    internal open fun loadPlan() {
        if (apiWrapper!!.hasNoInternetConnection()) {
            updateEmptyViewOnFailure(no_internet!!)
            return
        }

        try {
            apiResponseReceived(api!!.plan)
        } catch (e: NoInternetException) {
            updateEmptyViewOnFailure(no_internet!!)
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
            updateEmptyViewOnFailure(plan_failed_to_load!!)
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
    internal fun entryClicked(item: Any) {
        if (item is PlanRealEntryItem) {
            val entryItem = item
            val builder = AlertDialog.Builder(activity)
            builder.setItems(R.array.plan_options_array, PlanEntryClickListener(entryItem.showInformation!!, activity))
            builder.show()
        }

    }
}

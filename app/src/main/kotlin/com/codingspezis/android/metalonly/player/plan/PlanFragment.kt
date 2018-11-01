package com.codingspezis.android.metalonly.player.plan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.codingspezis.android.metalonly.player.R
import com.github.ironjan.metalonly.client.MetalOnlyClientV2
import com.github.ironjan.metalonly.client.NoInternetException
import com.github.ironjan.metalonly.client.model.PlanEntry
import com.hypertrack.hyperlog.HyperLog
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
import org.springframework.web.client.RestClientException

@EFragment(R.layout.fragment_plan)
@SuppressLint("SimpleDateFormat", "Registered")
open class PlanFragment : Fragment() {

    @JvmField
    @FragmentArg
    var site: String? = null

    @JvmField
    @ViewById(android.R.id.list)
    var list: ListView? = null

    @JvmField
    @ViewById
    var loadingMessageTextView: TextView? = null
    @JvmField
    @ViewById(android.R.id.progress)
    var loadingProgressBar: ProgressBar? = null
    @JvmField
    @StringRes
    var plan_failed_to_load: String? = null
    @JvmField
    @ViewById(android.R.id.empty)
    var empty: View? = null

    @JvmField
    @Bean
    var planEntryToItemConverter: PlanEntryToItemConverter? = null

    @JvmField
    @StringRes
    var plan: String? = null
    @JvmField
    @StringArrayRes
    var days: Array<String>? = null
    @JvmField
    @StringRes
    var no_internet: String? = null

    @AfterViews
    internal fun bindEmptyViewToList() {
        list!!.emptyView = empty
    }

    private val TAG: String = "PlanFragment"

    @AfterViews
    @Background
    internal open fun loadPlan() {
        try {
            if (context == null) return
            HyperLog.d(TAG, "loadPlan()")
            val either = MetalOnlyClientV2.getClient(context!!).getPlan()
            if (either.isRight()) {
                either.map(this::apiResponseReceived)
            } else {
                either.mapLeft(this::updateEmptyViewOnFailure)
            }
        } catch (e: NoInternetException) {
            HyperLog.d(TAG, "loadPlan() - no internet")
            updateEmptyViewOnFailure(no_internet)
        } catch (e: RestClientException) {
            HyperLog.d(TAG, "loadPlan() - RestClientException", e)
            // TODO Can we catch ResourceAccessException to HttpStatusCodeException show better info?
            val text = plan_failed_to_load + ":\n" + e.message
            updateEmptyViewOnFailure(text)
        }
    }

    @UiThread
    internal open fun apiResponseReceived(plan: Array<PlanEntry>) {
        HyperLog.d(TAG, "apiResponseReceived(...)")

        if (plan == null) {
            HyperLog.d(TAG, "apiResponseReceived() - plan was null")
            updateEmptyViewOnFailure(plan_failed_to_load)
            return
        }
        if (activity == null) {
            HyperLog.d(TAG, "apiResponseReceived() - activity is null")
            return
        }

        HyperLog.d(TAG, "apiResponseReceived() - Neither plan nor activitiy was not null, updating ui")

        val shows = plan

        val listItems = planEntryToItemConverter!!.convertToPlan(shows)
        if (activity != null) {
            val adapter = PlanAdapter(activity!!, listItems)

            list!!.adapter = adapter
            list!!.setSelection(planEntryToItemConverter!!.todayStartIndex())
        }

        HyperLog.d(TAG, "apiResponseReceived() - done")
    }

    @UiThread
    internal open fun updateEmptyViewOnFailure(text: String?) {
        if (text != null) {
            loadingMessageTextView?.text = text
            loadingProgressBar?.visibility = View.GONE
        }
    }

    @ItemClick(android.R.id.list)
    internal fun entryClicked(item: Any) {
        if (item is PlanRealEntryItem) {
            val entryItem = item
            val activity = activity
            if (activity != null) {
                val builder = AlertDialog.Builder(activity)
                builder.setItems(R.array.plan_options_array, PlanEntryClickListener(entryItem.planEntry!!, activity!!))
                builder.show()
            }
        }
    }
}

package de.ironjan.metalonly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import arrow.core.right
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.PlanEntry
import de.ironjan.metalonly.log.LW
import kotlinx.android.synthetic.main.fragment_plan.*

/**
 * FIXME add list
 * FIXME add empty view, loading fail, etc.
 */
class PlanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh()
    }
    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {

        val lContext = context ?: return
        val either = Client(lContext).getPlan()
        LW.d(TAG, "Start loading")

        if (either.isLeft()) {
            either.mapLeft {
                LW.d(TAG, "Loading plan failed: $it")
                activity?.runOnUiThread { Toast.makeText(lContext, "Loading fail", Toast.LENGTH_LONG).show() }
            }
        } else {
            LW.d(TAG, "Loading done")
            either.map { showPlan(it) }
        }
    }

    private fun showPlan(plan: List<PlanEntry>) {
        val txt = plan.sortedBy { e -> e.start }.map {
            val s =
                "${it.start} - ${it.end}: ${it.showInformation.show} by ${it.showInformation.moderator}"
            s
        }.joinToString("\n")
        LW.d(TAG, txt)
        activity?.runOnUiThread { txtDemo.text = txt + " " + plan.size }
        LW.d(TAG, "Showing done")

    }

    companion object {
    private const val TAG = "PlanFragment"
}
}

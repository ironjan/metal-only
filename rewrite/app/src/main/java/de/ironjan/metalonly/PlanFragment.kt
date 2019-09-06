package de.ironjan.metalonly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.ListFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arrow.core.right
import de.ironjan.metalonly.api.Client
import de.ironjan.metalonly.api.model.PlanEntry
import de.ironjan.metalonly.api.model.ShowInfo
import de.ironjan.metalonly.log.LW
import de.ironjan.metalonly.plan.PlanRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_plan.*

/**
 * FIXME add list
 * FIXME add empty view, loading fail, etc.
 */
class PlanFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val adapter = PlanRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lContext = context ?: return

        recyclerView = view.findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = LinearLayoutManager(lContext)

            // specify an viewAdapter (see also next example)
            adapter = this@PlanFragment.adapter

        }

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

    private fun showPlan(plan: List<PlanEntry>) = adapter.setPlan(plan)

    companion object {
        private const val TAG = "PlanFragment"
    }
}

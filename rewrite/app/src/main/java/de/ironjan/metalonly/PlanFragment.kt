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
        fakeRefresh()


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

    private fun fakeRefresh() {
        val mHead = ShowInfo("MetalHead", "Keine Grüsse und Wünsche möglich.", "Mixed Metal")

        val plan = listOf(
                PlanEntry("2019-09-02T00:00", "2019-09-02T15:00", mHead),
                PlanEntry("2019-09-02T15:00", "2019-09-02T18:00", ShowInfo("Wolle", "Slow Motion", "Stoner-, Doom, Grunge und Souther- Psychedelic Rock & Metal")),
                PlanEntry("2019-09-02T18:00", "2019-09-02T21:00", ShowInfo("Mick", "New Limits", "NEW Mixed Rock & Metal")),
                PlanEntry("2019-09-02T21:00", "2019-09-02T23:00", ShowInfo("MaRs", "MaRs' Metalmix", "Mixed Rock & Metal")),
                PlanEntry("2019-09-02T23:00", "2019-09-03T15:00", mHead),
                PlanEntry("2019-09-03T15:00", "2019-09-03T18:00", ShowInfo("Blacky", "Metal Mayhem", "Mixed Rock & Metal")),
                PlanEntry("2019-09-03T18:00", "2019-09-03T21:00", ShowInfo("Vincent", "Krachen im Gebälk", "Death, Thrash, Pagan, Folk Metal")),
                PlanEntry("2019-09-03T21:00", "2019-09-03T23:00", ShowInfo("Slaine", "METALFORCE WARP SPASM", "Mixed Metal")),
                PlanEntry("2019-09-03T23:00", "2019-09-04T15:00", mHead),
                PlanEntry("2019-09-04T15:00", "2019-09-04T18:00", ShowInfo("Slaine", "METALFORCE", "Mixed Rock & Metal")),
                PlanEntry("2019-09-04T18:00", "2019-09-04T20:00", ShowInfo("Wolle", "Satan was a Biker!", "Hard Rock, Heavy-, Speed-, Thrash Metal")),
                PlanEntry("2019-09-04T20:00", "2019-09-04T23:00", ShowInfo("MaRs", "Loud & Heavy", "Death & Blackmetal")),
                PlanEntry("2019-09-04T23:00", "2019-09-05T15:00", mHead),
                PlanEntry("2019-09-05T15:00", "2019-09-05T17:00", mHead),
                PlanEntry("2019-09-05T17:00", "2019-09-05T20:00", ShowInfo("Wolle", "Hoch die Hörner", "Folk-, Mittelalter-, Pagan- & Viking Metal")),
                PlanEntry("2019-09-05T20:00", "2019-09-05T23:00", ShowInfo("MaRs", "MaRs' Metalmix", "Mixed Rock & Metal")),
                PlanEntry("2019-09-05T23:00", "2019-09-06T14:00", mHead),
                PlanEntry("2019-09-06T14:00", "2019-09-06T16:00", ShowInfo("Slaine", "HEARTLANDS", "Folk und Mittelalter Rock und Metal")),
                PlanEntry("2019-09-06T16:00", "2019-09-06T18:00", ShowInfo("Blacky", "Metal Mayhem - Dark Edition", "Black-, Death-, Doom-, Pagan & Viking Metal")),
                PlanEntry("2019-09-06T18:00", "2019-09-06T20:00", ShowInfo("Wolle", "Metal Weekeeeend", "Mixed Rock &  Metal")),
                PlanEntry("2019-09-06T20:00", "2019-09-06T23:00", ShowInfo("Yoda", "Yodas Thrash Attack", "Speed, Thrash Metal")),
                PlanEntry("2019-09-06T23:00", "2019-09-07T14:00", mHead),
                PlanEntry("2019-09-07T14:00", "2019-09-07T17:00", ShowInfo("Wolle", "Einmal Musik bitte, mit allem!", "Mixed Rock & Metal")),
                PlanEntry("2019-09-07T17:00", "2019-09-07T19:00", ShowInfo("frei", "", "")),
                PlanEntry("2019-09-07T19:00", "2019-09-07T23:00", ShowInfo("Slaine", "Metalforce", "Mixed Rock & Metal")),
                PlanEntry("2019-09-07T23:00", "2019-09-08T14:00", mHead),
                PlanEntry("2019-09-08T14:00", "2019-09-08T18:00", ShowInfo("Godshand", "Godis 2nd Hand Musikladen", "Mixed Rock & Metal")),
                PlanEntry("2019-09-08T18:00", "2019-09-08T20:00", ShowInfo("frei", "", "")),
                PlanEntry("2019-09-08T20:00", "2019-09-08T23:00", ShowInfo("Wolle", "Oldies und Klassiker der Rockgeschichte!", "60er bis Mitte 90er Rock & Metal")),
                PlanEntry("2019-09-08T23:00", "2019-09-09T00:00", mHead))


        showPlan(plan)

    }

    private fun showPlan(plan: List<PlanEntry>) = adapter.setPlan(plan)

    companion object {
        private const val TAG = "PlanFragment"
    }
}

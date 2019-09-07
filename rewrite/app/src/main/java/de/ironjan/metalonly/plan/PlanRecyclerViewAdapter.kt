package de.ironjan.metalonly.plan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.koushikdutta.ion.Ion
import de.ironjan.metalonly.R
import de.ironjan.metalonly.api.model.PlanEntry
import de.ironjan.metalonly.log.LW
import java.text.SimpleDateFormat
import java.util.*

class PlanRecyclerViewAdapter : RecyclerView.Adapter<PlanEntryViewHolder>() {
    private val plan = mutableListOf<PlanEntry>()

    fun setPlan(newPlan: List<PlanEntry>) {
        plan.clear()
        plan.addAll(newPlan)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_plan_entry, parent, false) as ConstraintLayout

        return PlanEntryViewHolder(view)
    }

    override fun getItemCount(): Int = plan.size

    override fun onBindViewHolder(holder: PlanEntryViewHolder, position: Int) {
        val entry = plan[position]
        val show = entry.showInformation

        holder.itemView.findViewById<TextView>(R.id.txtModerator).text = show.moderator
        holder.itemView.findViewById<TextView>(R.id.txtShow).text = show.show
        holder.itemView.findViewById<TextView>(R.id.txtShow).text = show.genre


        val dayDateFormat = SimpleDateFormat("dd'.'", Locale.GERMAN)

        holder.itemView.findViewById<TextView>(R.id.txtDate).text = dayDateFormat.format(entry.startDateTime)
        holder.itemView.findViewById<TextView>(R.id.txtStartTime).text = entry.startTime
        holder.itemView.findViewById<TextView>(R.id.txtEndTime).text = entry.endTime


        val imgMod = holder.itemView.findViewById<ImageView>(R.id.imgMod) ?: return

        // fixme duplicate code
        val context = holder.itemView.context ?: return
        val id = context.resources.getIdentifier(show.moderator.toLowerCase(), "drawable", context.packageName)
        val modUrl = "https://www.metal-only.de/botcon/mob.php?action=pic&nick=${show.moderator}"
        LW.d("PlanRecyclerViewAdapter", "Mod image not delivered via app. Loading from URL.")
        Ion.with(imgMod)
                .placeholder(R.drawable.metalhead)
                // TODO do we need these?
//                        .error(R.drawable.error_image)
//                        .animateLoad(spinAnimation)
//                        .animateIn(fadeInAnimation)
                .load(modUrl)

        // todo bind times...
    }


}

class PlanEntryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

}

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
        val view = when (viewType) {
            VIEW_TYPE_ENTRY -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_plan_entry, parent, false) as ConstraintLayout
            VIEW_TYPE_HEADER -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_plan_entry_header, parent, false) as ConstraintLayout
            else -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_plan_entry, parent, false) as ConstraintLayout
        }

        return PlanEntryViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if(position <= 0) {
            return VIEW_TYPE_HEADER
        }

        val dayPosMinus1 = dayDateFormat.format(plan[position-1].startDateTime)
        val dayPos= dayDateFormat.format(plan[position].startDateTime)
        if(!dayPosMinus1.equals(dayPos)) {
            return VIEW_TYPE_HEADER
        }
        return VIEW_TYPE_ENTRY
    }

    override fun getItemCount(): Int = plan.size

    override fun onBindViewHolder(holder: PlanEntryViewHolder, position: Int) {
        val entry = plan[position]
        val show = entry.showInformation

        holder.itemView.findViewById<TextView>(R.id.txtModerator).text = show.moderator
        holder.itemView.findViewById<TextView>(R.id.txtShow).text = show.show
        holder.itemView.findViewById<TextView>(R.id.txtShow).text = show.genre


        val day = dayDateFormat.format(entry.startDateTime)
        holder.itemView.findViewById<TextView>(R.id.txtDate).text = day

        val sTime = entry.startTime //timeDateFormat.format(entry.startDateTime)
        holder.itemView.findViewById<TextView>(R.id.txtStartTime).text = sTime

        val eTime = entry.endTime //timeDateFormat.format(entry.endDateTime)
        holder.itemView.findViewById<TextView>(R.id.txtEndTime).text = eTime

        val imgMod = holder.itemView.findViewById<ImageView>(R.id.imgMod) ?: return

        // fixme duplicate code
        // fixme use resource if available
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

    companion object {
        private const val VIEW_TYPE_HEADER = 1
        private const val VIEW_TYPE_ENTRY = 2
        val dayDateFormat = SimpleDateFormat("cc", Locale.GERMAN)
        val timeDateFormat = SimpleDateFormat("HH:mm", Locale.GERMAN)
    }
}

class PlanEntryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

}

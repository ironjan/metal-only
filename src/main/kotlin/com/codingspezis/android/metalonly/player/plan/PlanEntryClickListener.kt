package com.codingspezis.android.metalonly.player.plan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import com.codingspezis.android.metalonly.player.R

/**
 * ClickListener for entries in the plan shown by [PlanActivity]
 */
class PlanEntryClickListener(private val data: ShowInformation, private val context: Context) : DialogInterface.OnClickListener {

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            ADD_TO_CALENDAR_ACTION -> addEntryToCalendar()
            SHARE_ACTION -> shareEntry()
        }
    }

    private fun addEntryToCalendar() {
        val intent = Intent(Intent.ACTION_EDIT)
        val description = data.getModerator() + "\n" + data.getGenre()

        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra("title", data.getShowTitle())
        intent.putExtra("description", description)
        intent.putExtra("beginTime", data.getStartDate().time)
        intent.putExtra("endTime", data.getEndDate().time)

        context.startActivity(intent)
    }

    private fun shareEntry() {
        val message = """|${formattedDateString()} ${startTimeString()} - ${endTimeString()}
                         |${data.getShowTitle()}
                         |${data.getModerator()}
                         |${data.getGenre()}""".trimMargin()

        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        context.startActivity(Intent.createChooser(share,
                context.resources.getStringArray(R.array.plan_options_array)[1]))
    }

    fun formattedDateString(): String {
        return PlanEntryDateHelper.formattedDateString(data)
    }

    fun startTimeString(): CharSequence {
        return PlanEntryDateHelper.startTimeString(data)
    }

    fun endTimeString(): String {
        return PlanEntryDateHelper.endTimeString(data)
    }

    companion object {
        private val ADD_TO_CALENDAR_ACTION = 0
        private val SHARE_ACTION = 1
    }
}
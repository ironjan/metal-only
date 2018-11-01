package com.codingspezis.android.metalonly.player.plan

import com.github.ironjan.metalonly.client.model.PlanEntry
import java.text.SimpleDateFormat

/**
 * Singleton helper class to convert {@link ShowInformation} dates to readable {@link String}s.
 */
class ShowInformationDateHelper {
    companion object {
        private val DATE_FORMAT_TIME = SimpleDateFormat("HH:mm")
        private val DATE_FORMAT_DATE = SimpleDateFormat("dd.MM.yy")

        fun formattedDateString(data: PlanEntry): String {
            return DATE_FORMAT_DATE.format(data.start.time)
        }

        fun startTimeString(data: PlanEntry): CharSequence {
            return DATE_FORMAT_TIME.format(data.start.time)
        }

        fun endTimeString(data: PlanEntry): String {
            return DATE_FORMAT_TIME.format(data.end.time)
        }

        /**
          * This method is the only one annotated with @JvmStatic because it is
          * the only one called from Java code.
          * @return time information, e.g. "14:00 - 16:00"
          */
        @JvmStatic
        fun fullTimeString(data: PlanEntry): String {
            return "${startTimeString(data)} - ${endTimeString(data)}"
        }
    }
}
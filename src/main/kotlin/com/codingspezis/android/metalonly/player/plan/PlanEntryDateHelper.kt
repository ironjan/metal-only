package com.codingspezis.android.metalonly.player.plan

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class PlanEntryDateHelper() {
    companion object {
        @SuppressLint("SimpleDateFormat")
        private val DATE_FORMAT_TIME = SimpleDateFormat("HH:mm")
        @SuppressLint("SimpleDateFormat")
        private val DATE_FORMAT_DATE = SimpleDateFormat("dd.MM.yy")
        @SuppressLint("SimpleDateFormat")
        private val DATE_FORMAT_DATE_DAY = SimpleDateFormat("dd")

        fun formattedDateString(data: PlanData): String {
            return DATE_FORMAT_DATE.format(data.start.time)
        }

        fun startTimeString(data: PlanData): CharSequence {
            return DATE_FORMAT_TIME.format(data.start.time)
        }

        fun endTimeString(data: PlanData): String {
            return DATE_FORMAT_TIME.format(data.end.time)
        }

        /**
          * This method is the only one annotated with @JvmStatic because it is
          * the only one called from Java code.
          * @return time information, e.g. "14:00 - 16:00"
          */
        @JvmStatic
        fun fullTimeString(data: PlanData): String {
            return "${startTimeString(data)} - ${endTimeString(data)}"
        }
    }
}
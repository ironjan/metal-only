package de.ironjan.metalonly.api.model

import java.text.SimpleDateFormat
import java.util.*

data class PlanEntry(
    val start: String,
    val end: String,
    val showInformation: ShowInfo
){
    val startDateTime
        get() = simpleDateFormat.parse(start)

    val startDay
            get() = start.substring(8, 10)

    val endDateTime
        get() = simpleDateFormat.parse(end)

    val startTime
        get() = start.substring(11,16)
    val endTime
        get() = end.substring(11,16)

    companion object {
        private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.GERMAN)
    }
}


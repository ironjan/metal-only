package de.ironjan.metalonly.api.model

import java.text.SimpleDateFormat

data class PlanEntry(
    val start: String,
    val end: String,
    val showInformation: ShowInfo
){
    val startDateTime
        get() = simpleDateFormat.parse(start)
    val endDateTime
        get() = simpleDateFormat.parse(end)
    val startDate
        get() = start.substring(0,10)

    val startTime
        get() = start.substring(12,16)
    val endTime
        get() = end.substring(12,16)

    companion object {
        private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
    }
}


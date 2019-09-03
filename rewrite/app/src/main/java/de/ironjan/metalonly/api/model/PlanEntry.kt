package de.ironjan.metalonly.api.model

import java.text.SimpleDateFormat

data class PlanEntry(
    val start: String,
    val end: String,
    val showInformation: ShowInfo
){
    val startDateTime = simpleDateFormat.parse(start)
    val endDateTime = simpleDateFormat.parse(end)

    companion object {
        private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
    }
}


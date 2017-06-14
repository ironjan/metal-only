package com.codingspezis.android.metalonly.player.plan

import java.util.Date

/**
 * Interface for ShowInformation.
 */
interface ShowInformation {
    val moderator: String
    val genre: String
    val showTitle: String
    val startDate: Date
    val endDate: Date
}

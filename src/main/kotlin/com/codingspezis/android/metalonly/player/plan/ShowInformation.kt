package com.codingspezis.android.metalonly.player.plan

import java.util.Date

/**
 * Unification interface. Combines the common ops between both data classes. The operations are
 * explicitely named differently to enforce delegation to existing methods.
 */
interface ShowInformation {
    val moderator: String
    val genre: String
    val showTitle: String
    val startDate: Date
    val endDate: Date
}

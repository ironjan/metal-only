package com.codingspezis.android.metalonly.player.core

import java.util.Date

/**
 * Interface for ShowInformation.
 */
interface ShowInformation : BasicShowInformation, ShowTimeInformation {
    override val moderator: String
    override val genre: String
    override val showTitle: String
    override val startDate: Date
    override val endDate: Date
}

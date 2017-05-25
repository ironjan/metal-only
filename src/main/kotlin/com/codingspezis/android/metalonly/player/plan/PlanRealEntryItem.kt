package com.codingspezis.android.metalonly.player.plan

/**
 * Represents a "real" entry in the plan, i.e. a moderated show
 */
class PlanRealEntryItem(val showInformation: ShowInformation?) : PlanItem {

    override val isSection: Boolean
        get() = false

}
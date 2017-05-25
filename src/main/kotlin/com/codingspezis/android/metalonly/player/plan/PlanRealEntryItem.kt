package com.codingspezis.android.metalonly.player.plan

/**
 * Represents a "real" entry in the plan, i.e. a moderated show
 */
class PlanRealEntryItem(data: ShowInformation) : PlanItem() {

    init {
        this.planData = data
    }

    override val isSection: Boolean
        get() = false

}
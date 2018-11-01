package com.codingspezis.android.metalonly.player.plan

import com.github.ironjan.metalonly.client.model.PlanEntry

/**
 * Represents a "real" entry in the plan, i.e. a moderated show
 */
class PlanRealEntryItem(val planEntry: PlanEntry) : PlanItem {

    override val isSection: Boolean
        get() = false
}
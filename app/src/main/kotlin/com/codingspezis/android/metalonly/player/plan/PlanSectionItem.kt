package com.codingspezis.android.metalonly.player.plan

/**
 * Represents a divider between days in plan.
 */
class PlanSectionItem(val title: String) : PlanItem {

    override val isSection: Boolean
        get() = true
}
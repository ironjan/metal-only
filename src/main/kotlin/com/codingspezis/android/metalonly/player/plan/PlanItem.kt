package com.codingspezis.android.metalonly.player.plan

/**
 * Represents a plan entry.
 *
 *
 * Base class for [PlanRealEntryItem], [PlanSectionItem]
 */
abstract class PlanItem {

    @JvmField
    internal var planData: ShowInformation? = null

    abstract val isSection: Boolean

}
package com.codingspezis.android.metalonly.player.plan;

/**
 * Represents a plan entry.
 * <p/>
 * Base class for {@link PlanRealEntryItem}, {@link PlanSectionItem}
 */
public abstract class PlanItem {

    protected PlanData data = null;

    public PlanData getPlanData() {
        return data;
    }

    public abstract boolean isSection();

}
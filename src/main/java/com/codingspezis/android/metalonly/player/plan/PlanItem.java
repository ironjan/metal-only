package com.codingspezis.android.metalonly.player.plan;

/**
 * Represents a plan entry.
 * <p/>
 * Base class for {@link PlanRealEntryItem}, {@link PlanSectionItem}
 */
public abstract class PlanItem {

    protected ShowInformation data = null;

    public ShowInformation getPlanData() {
        return data;
    }

    public abstract boolean isSection();

}
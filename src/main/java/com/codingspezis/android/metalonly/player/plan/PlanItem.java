package com.codingspezis.android.metalonly.player.plan;

/**
 * Represents a plan entry.
 * <p/>
 * Base class for {@link PlanRealEntryItem}, {@link PlanSectionItem}
 */
public abstract class PlanItem {

    protected PlanEntryAndDataUnification data = null;

    public PlanEntryAndDataUnification getPlanData() {
        return data;
    }

    public abstract boolean isSection();

}
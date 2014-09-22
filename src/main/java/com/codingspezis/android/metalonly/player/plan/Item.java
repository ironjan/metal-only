package com.codingspezis.android.metalonly.player.plan;

/**
 * Represents a plan entry.
 * <p/>
 * Base class for {@link EntryItem}, {@link SectionItem}
 */
public abstract class Item {

    protected PlanData data = null;

    public PlanData getPlanData() {
        return data;
    }

    public abstract boolean isSection();

}
package com.codingspezis.android.metalonly.player.plan;

/**
 * Represents a "real" entry in the plan, i.e. a moderated show
 */
public class PlanRealEntryItem extends PlanItem {

    public PlanRealEntryItem(ShowInformation data) {
        this.data = data;
    }

    @Override
    public boolean isSection() {
        return false;
    }

}
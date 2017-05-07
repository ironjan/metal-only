package com.codingspezis.android.metalonly.player.plan;

/**
 * Represents a divider between days in plan.
 */
public class PlanSectionItem extends PlanItem {

    private final String title;

    public PlanSectionItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

}
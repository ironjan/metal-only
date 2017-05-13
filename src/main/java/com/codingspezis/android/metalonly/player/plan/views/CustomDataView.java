package com.codingspezis.android.metalonly.player.plan.views;

import com.codingspezis.android.metalonly.player.plan.PlanEntryAndDataUnification;

/**
 * Interface to provide a binding method.
 */
public interface CustomDataView<T> {
    /**
     * Binds t to the view
     *
     * @param planData the item to be displayed
     */
    void bind(PlanEntryAndDataUnification planData);
}

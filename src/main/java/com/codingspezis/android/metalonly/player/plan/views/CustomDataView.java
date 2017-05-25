package com.codingspezis.android.metalonly.player.plan.views;

/**
 * Provides a data binding method for custom views.
 */
public interface CustomDataView<T> {
    /**
     * Binds t to the view
     *
     * @param t the item to be displayed
     */
    void bind(T t);
}

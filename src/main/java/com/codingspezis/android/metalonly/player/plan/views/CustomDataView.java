package com.codingspezis.android.metalonly.player.plan.views;

/**
 * Interface to provide a binding method.
 */
public interface CustomDataView<T> {
    /**
     * Binds t to the view
     *
     * @param t the item to be displayed
     */
    public void bind(T t);
}

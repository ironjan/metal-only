package com.codingspezis.android.metalonly.player.plan;

import java.util.Date;

/**
 * Unification interface. Combines the common ops between both data classes. The operations are
 * explicitely named differently to enforce delegation to existing methods.
 */
public interface ShowInformation {
    String getModerator();
    String getGenre();
    String getShowTitle();
    Date getStartDate();
    Date getEndDate();
}

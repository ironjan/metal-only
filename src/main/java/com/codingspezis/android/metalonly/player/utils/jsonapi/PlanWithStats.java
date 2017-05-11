package com.codingspezis.android.metalonly.player.utils.jsonapi;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * <pre>
 * {
 *  "stats":
 *   {
 *    "moderated": false,
 *    "moderator": "MetalHead",
 *    "sendung": "Keine Gruesse und Wuensche moeglich. (Mixed Metal)",
 *    "wunschvoll": "1",
 *    "grussvoll": "1",
 *    "wunschlimit": "0",
 *    "grusslimit": "0"
 *   },
 *   "plan": [
 *    {
 *     "day": "29.07.13",
 *     "time": "00:00",
 *     "duration": 15,
 *     "moderator": "MetalHead",
 *     "show": "Keine Gruesse und Wuensche moeglich.",
 *     "genre": "Mixed Metal"
 *    }
 *   ]
 * }
 * </pre>
 */
@JsonAutoDetect
public class PlanWithStats {
    Stats stats = Stats.getDefault();
    PlanEntry[] plan = {};

    public static PlanWithStats getDefault() {
        return new PlanWithStats();
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public PlanEntry[] getPlan() {
        return plan;
    }

    public void setPlan(PlanEntry[] plan) {
        this.plan = plan;
    }

}

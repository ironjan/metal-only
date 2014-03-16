package com.codingspezis.android.metalonly.player.utils.jsonapi;

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
public class PlanWithStats {
    Stats stats = new Stats();
    PlanEntry[] plan = {};

    public Stats getStats() {
        return stats;
    }

    public PlanEntry[] getPlan() {
        return plan;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public void setPlan(PlanEntry[] plan) {
        this.plan = plan;
    }

}

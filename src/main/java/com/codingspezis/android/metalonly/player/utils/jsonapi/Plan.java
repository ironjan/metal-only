package com.codingspezis.android.metalonly.player.utils.jsonapi;

/**
 * <pre>
 * {
 *  "plan": [
 *   {
 *    "day": "29.07.13",
 *    "time": "00:00",
 *    "duration": 15,
 *    "moderator": "MetalHead",
 *    "show": "Keine Gruesse und Wuensche moeglich.",
 *    "genre": "Mixed Metal"
 *   }
 *  ]
 * }
 * </pre>
 */
public class Plan {
    private Plan(){}

    PlanEntry[] plan = {};

    public PlanEntry[] getPlan() {
        return plan;
    }

    public void setPlan(PlanEntry[] plan) {
        this.plan = plan;
    }

    public static Plan getDefault() {
        return new Plan();
    }


}

package com.github.ironjan.metalonly.client_library;

import com.codingspezis.android.metalonly.player.core.ShowInformation;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * {
 *  "plan": [
 *   {
 *    "day": "29.07.13",
 *    "time": "00:00",
 *    "duration": 15,
 *    "getModerator": "MetalHead",
 *    "show": "Keine Gruesse und Wuensche moeglich.",
 *    "getGenre": "Mixed Metal"
 *   }
 *  ]
 * }
 * </pre>
 */
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plan {
    List<ShowInformation> entries = new ArrayList<>();

    public List<ShowInformation> getEntries(){
        List<ShowInformation> copy = new ArrayList<>(entries);
        copy.addAll(entries);
        return copy;
    }

    @JsonProperty("plan")
    public void setPlanEntries(PlanEntry[] plan) {
        entries.clear();
        Collections.addAll(entries, plan);
    }


}

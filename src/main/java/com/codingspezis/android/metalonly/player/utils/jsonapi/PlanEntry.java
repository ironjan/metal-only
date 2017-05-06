package com.codingspezis.android.metalonly.player.utils.jsonapi;

import android.annotation.SuppressLint;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 * {
 *  "day": "29.07.13",
 *  "time": "00:00",
 *  "duration": 15,
 *  "moderator": "MetalHead",
 *  "show": "Keine Gruesse und Wuensche moeglich.",
 *  "genre": "Mixed Metal"
 * }
 * </pre>
 */
@JsonAutoDetect
public class PlanEntry {
    @SuppressLint("SimpleDateFormat")
    public PlanEntry(){
        // Set value here to catch wrong formats
        dateStringFormat = new SimpleDateFormat("dd'.'MM'.'yy'T'HH':'mm");
    }

    private static final String DAY_TIME_DIVIDER = "T";
    private final SimpleDateFormat dateStringFormat;
    private String day, time, moderato, show, genre;
    private int duration;



    public Date getStartDate() {
        String dateString = day + DAY_TIME_DIVIDER + time;
        Date startDate = null;
        try {
            startDate = dateStringFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(PlanEntry.class.getSimpleName(), "Error when parsing \""
                    + dateString + "\" into date.");
        }
        return startDate;
    }

    public String getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(String time) {
        this.time = time;
    }

    public String getModerato() {
        return moderato;
    }

    @JsonProperty("moderator")
    public void setModerato(String moderato) {
        this.moderato = moderato;
    }

    public String getShow() {
        return show;
    }

    @JsonProperty("show")
    public void setShow(String show) {
        this.show = show;
    }

    public String getGenre() {
        return genre;
    }

    @JsonProperty("genre")
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    @JsonProperty("duration")
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @JsonProperty("day")
    public void setDay(String day) {
        this.day = day;
    }

}

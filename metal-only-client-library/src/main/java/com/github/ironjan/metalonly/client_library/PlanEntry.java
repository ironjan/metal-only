package com.github.ironjan.metalonly.client_library;

import android.annotation.SuppressLint;
import android.util.Log;

import com.codingspezis.android.metalonly.player.core.ShowInformation;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 * {
 *  "day": "29.07.13",
 *  "time": "00:00",
 *  "durationInHours": 15,
 *  "getModerator": "MetalHead",
 *  "show": "Keine Gruesse und Wuensche moeglich.",
 *  "getGenre": "Mixed Metal"
 * }
 * </pre>
 */
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanEntry implements ShowInformation {
    private static final String DAY_TIME_DIVIDER = "T";
    private final SimpleDateFormat dateStringFormat;
    private String day, time, moderato, show, genre;
    private int durationInHours;

    @SuppressLint("SimpleDateFormat")
    public PlanEntry(){
        // Set value here to catch wrong formats
        dateStringFormat = new SimpleDateFormat("dd'.'MM'.'yy'T'HH':'mm");
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

    @JsonProperty("genre")
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDurationInHours() {
        return durationInHours;
    }

    @JsonProperty("duration")
    public void setDurationInHours(int durationInHours) {
        this.durationInHours = durationInHours;
    }

    @JsonProperty("day")
    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String getModerator() {
        return getModerato();
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public String getShowTitle() {
        return getShow();
    }

    @Override
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

    @Override
    public Date getEndDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartDate());
        cal.add(Calendar.HOUR, durationInHours);
        return cal.getTime();
    }
}

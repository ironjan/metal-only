package com.codingspezis.android.metalonly.player.utils.jsonapi;

import android.annotation.SuppressLint;
import android.util.Log;

import com.codingspezis.android.metalonly.player.plan.ShowInformation;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
 *  "moderator": "MetalHead",
 *  "show": "Keine Gruesse und Wuensche moeglich.",
 *  "genre": "Mixed Metal"
 * }
 * </pre>
 */
@JsonAutoDetect
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
    public String moderator() {
        return getModerato();
    }

    @Override
    public String genre() {
        return getGenre();
    }

    @Override
    public String showTitle() {
        return getShow();
    }

    @Override
    public Date start() {
        return getStartDate();
    }

    @Override
    public Date end() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartDate());
        cal.add(Calendar.HOUR, durationInHours);
        return cal.getTime();
    }
}

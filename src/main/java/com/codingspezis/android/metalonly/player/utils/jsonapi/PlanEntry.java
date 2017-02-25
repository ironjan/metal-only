package com.codingspezis.android.metalonly.player.utils.jsonapi;

import android.annotation.SuppressLint;
import android.util.Log;

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
public class PlanEntry {
    private static final String DAY_TIME_DIVIDER = "T";
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat dateStringFormat = new SimpleDateFormat(
            "dd'.'MM'.'YY'T'HH':'mm");
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

    public void setTime(String time) {
        this.time = time;
    }

    public String getModerato() {
        return moderato;
    }

    public void setModerato(String moderato) {
        this.moderato = moderato;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDay(String day) {
        this.day = day;
    }

}

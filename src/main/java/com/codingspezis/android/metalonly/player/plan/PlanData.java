package com.codingspezis.android.metalonly.player.plan;

import com.codingspezis.android.metalonly.player.PlanActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class is an extended version of {@link com.codingspezis.android.metalonly.player.utils.jsonapi.PlanEntry}.
 * It contains a lot of the same information (they represent the same data) and some "utility" methods.
 * The later should be extracted to a more appropriate place (SRP) and {@link PlanData} should not
 * be used anymore.
 * @deprecated Use {@link com.codingspezis.android.metalonly.player.utils.jsonapi.PlanEntry} for new
 * classes.
 */
public class PlanData implements PlanEntryAndDataUnification{
    private final String mod, genre, title;
    private Calendar start;
    private int duration;

    public PlanData(String mod, String title, String genre) {
        this.title = title;
        this.genre = genre;
        this.mod = mod;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Calendar getEnd() {
        Calendar tmpCal = (Calendar) start.clone();
        tmpCal.add(Calendar.HOUR_OF_DAY, getDuration());
        return tmpCal;
    }

    public String getGenre() {
        return genre;
    }

    public String getMod() {
        return mod;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String moderator() {
        return getMod();
    }

    @Override
    public String genre() {
        return getGenre();
    }

    @Override
    public String showTitle() {
        return getTitle();
    }

    @Override
    public Date start() {
        return getStart().getTime();
    }

    @Override
    public Date end() {
        return getEnd().getTime();
    }
}
package com.codingspezis.android.metalonly.player.plan;

import com.codingspezis.android.metalonly.player.PlanActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class is an extended version of {@link com.codingspezis.android.metalonly.player.utils.jsonapi.PlanEntry}.
 * It contains a lot of the same information (they represent the same data) and some "utility" methods.
 * The later should be extracted to a more appropriate place (SRP) and {@link PlanData} should not
 * be used anymore.
 * @deprecated Use {@link com.codingspezis.android.metalonly.player.utils.jsonapi.PlanEntry} for new
 * classes.
 */
public class PlanData {
    private static final int HOUR_IN_MILLIS = 60 * 60 * 1000;
    private final String mod, genre, title;
    private Calendar start;
    private int duration;

    public PlanData(String mod, String title, String genre) {
        this.title = title;
        this.genre = genre;
        this.mod = mod;
    }

    public CharSequence getDateString() {
        CharSequence ret;
        if (getStart().get(Calendar.DAY_OF_WEEK) == getEnd().get(Calendar.DAY_OF_WEEK)
                || getEnd().get(Calendar.HOUR_OF_DAY) == 0) {
            ret = PlanActivity.DATE_FORMAT_DATE.format(getStart().getTime());
        } else {
            ret = PlanActivity.DATE_FORMAT_DATE_DAY.format(getStart().getTime()) + "/"
                    + PlanActivity.DATE_FORMAT_DATE.format(getEnd().getTime());
        }

        return ret;
    }

    public String getDescription() {
        return getTitle() + "\n" + getMod() + "\n" + getGenre() + "\n";
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

    public int getProgress() {
        Calendar cal = new GregorianCalendar();
        float timeToEnd = getEnd().getTimeInMillis() - cal.getTimeInMillis();
        float durationInMillis = getDuration() * HOUR_IN_MILLIS;

        return (int) ((timeToEnd / durationInMillis) * 100);
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public long getStartTimeAsMillis() {
        return getStart().getTimeInMillis();
    }

    public long getEndTimeAsMillis() {
        return getEnd().getTimeInMillis();
    }

    public CharSequence getTimeString() {
        return PlanActivity.DATE_FORMAT_TIME.format(start.getTime()) + " - "
                + PlanActivity.DATE_FORMAT_TIME.format(getEnd().getTime());
    }

    public String getTitle() {
        return title;
    }

    public boolean sameDay(PlanData other) {
        int thisDay = getStart().get(Calendar.DAY_OF_WEEK);
        int otherDay = other.getStart().get(Calendar.DAY_OF_WEEK);
        return thisDay == otherDay;
    }
}
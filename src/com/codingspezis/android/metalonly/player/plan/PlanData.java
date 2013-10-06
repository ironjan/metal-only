package com.codingspezis.android.metalonly.player.plan;

import java.util.*;

import com.codingspezis.android.metalonly.player.*;

public class PlanData {
	private final String mod, genre, title;
	private Calendar start;
	private int duration;

	public PlanData() {
		title = "";
		mod = "";
		genre = "";
	}

	public PlanData(String mod, String title, String genre) {
		this.title = title;
		this.genre = genre;
		this.mod = mod;
	}

	public CharSequence getDateString() {
		CharSequence ret;
		if (getStart().get(Calendar.DAY_OF_WEEK) == getEnd().get(
				Calendar.DAY_OF_WEEK)
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
		float timeToEnd = getEnd().getTimeInMillis()
				- cal.getTimeInMillis();
		float durationInMillis = getDuration() * 60 * 60 * 1000;
		return (int) ((timeToEnd / durationInMillis) * 100);

	}

	public Calendar getStart() {
		return start;
	}

	public long getStartTimeAsMillis(){
		return getStart().getTimeInMillis();
	}
	public long getEndTimeAsMillis(){
		return getEnd().getTimeInMillis();
	}
	
	public CharSequence getTimeString() {
		return PlanActivity.DATE_FORMAT_TIME.format(start.getTime()) + " - "
				+ PlanActivity.DATE_FORMAT_TIME.format(getEnd().getTime());
	}

	public String getTitle() {
		return title;
	}

	public boolean sameDay(PlanData d1) {
		return getStart().get(Calendar.DAY_OF_WEEK) == d1.getStart().get(
				Calendar.DAY_OF_WEEK);
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}
}
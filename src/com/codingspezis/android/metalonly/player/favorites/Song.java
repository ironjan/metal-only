package com.codingspezis.android.metalonly.player.favorites;

import java.util.*;

/**
 * class for holding a song
 */
public class Song {
	public final String interpret;
	public final String title;
	public final long date;

	private String thumb;

	/**
	 * Creates a Song with the given parameters
	 * 
	 * @param interpret
	 *            song artist
	 * @param title
	 *            song title
	 * @param thumb
	 *            name of moderator's picture
	 * @param date
	 *            when this song was played
	 */
	public Song(String interpret, String title, String thumb, long date) {
		super();
		this.interpret = interpret;
		this.title = title;
		this.thumb = thumb;
		this.date = date;
	}

	/**
	 * Wrapper for {@link #Song(String, String, String, long)} with date set to
	 * the moment this constructor is called
	 * 
	 * @param interpret
	 *            song artist
	 * @param title
	 *            song title
	 * @param thumb
	 *            name of moderator's picture
	 */
	public Song(String interpret, String title, String thumb) {
		super();
		this.interpret = interpret;
		this.title = title;
		this.thumb = thumb;
		this.date = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Wrapper for {@link #Song(String, String, String, long)}. Creates a song
	 * with out thumb and date set to the moment this constructor is called
	 * 
	 * @param interpret
	 *            song artist
	 * @param title
	 *            song title
	 */
	public Song(String interpret, String title) {
		this(interpret, title, "");
	}

	/**
	 * Removes the picture
	 */
	public void clearThumb() {
		thumb = "";
	}

	/**
	 * 
	 * @return true, if this is a valid song. False if invalid
	 */
	public boolean isValid() {
		return interpret.length() != 0 && title.length() != 0;
	}

	public String getThumb() {
		return thumb;
	}
}
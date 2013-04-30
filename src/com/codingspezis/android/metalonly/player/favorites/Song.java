package com.codingspezis.android.metalonly.player.favorites;

/**
 * 
 * class for holding a song
 */
public class Song {
	public String interpret = "";
	public String title = "";
	public String thumb = "";
	public long date = 0;

	public boolean isValid() {
		return interpret.length() != 0 && title.length() != 0;
	}
}
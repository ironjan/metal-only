package com.codingspezis.android.metalonly.player.favorites;

/**
 * Song
 * @version 03.02.2013
 *
 * class for holding a song
 */
public class Song{
	public String interpret = "";
	public String title = "";
	public String thumb = "";
	public long date = 0;
	public boolean isValid(){
		return interpret.length()!=0 && title.length()!=0;
	}
}
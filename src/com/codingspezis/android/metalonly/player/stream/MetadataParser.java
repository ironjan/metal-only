package com.codingspezis.android.metalonly.player.stream;

import java.util.*;


import com.codingspezis.android.metalonly.player.favorites.*;
import com.codingspezis.android.metalonly.player.favorites.SongSaver.*;
import com.codingspezis.android.metalonly.player.utils.*;

/**
 * MetadataParser
 * 
 * @version 27.12.2012
 * 
 *          parses interpret title genre and moderator from meta data
 * 
 */
public class MetadataParser {

	private String INTERPRET;
	private String TITLE;
	private String GENRE;
	private String MODERATOR;

	public MetadataParser(String data) {
		try {
			if (numberOfStars(data) >= 3) {
				String[] slices = data.split("\\*");
				MODERATOR = slices[1].trim();
				GENRE = slices[2].trim();
				data = slices[0].trim();
			} else {
				MODERATOR = "MetalHead OnAir";
				GENRE = "Mixed Metal";
			}
		} catch (Exception e) {
			MODERATOR = "";
			GENRE = "";
		}
		try {
			INTERPRET = data.substring(0, data.indexOf(" - ")).trim();
			TITLE = data.substring(data.indexOf(" - ") + 2).trim();
		} catch (Exception e) {
			INTERPRET = "";
			TITLE = "";
		}
	}

	public String getINTERPRET() {
		return INTERPRET;
	}

	public String getTITLE() {
		return TITLE;
	}

	public String getGENRE() {
		return GENRE;
	}

	public String getMODERATOR() {
		return MODERATOR;
	}

	public Song toSong() {
		Song song = new Song();
		song.interpret = INTERPRET;
		song.title = TITLE;
		song.thumb = MODERATOR;
		if (song.thumb.indexOf(" OnAir") > 0) {
			song.thumb = song.thumb.substring(0,
					song.thumb.indexOf(" OnAir")).trim();
		}
		song.date = Calendar.getInstance().getTimeInMillis();
		return song;
	}

	/**
	 * checks string str for occurrence of '*'
	 * 
	 * @param str
	 *            string to check
	 * @return number of char '*' containing in str
	 */
	private int numberOfStars(String str) {
		if (str.length() == 0) {
			return 0;
		}
		if (str.charAt(0) == '*') {
			return 1 + numberOfStars(str.substring(1));
		} else {
			return numberOfStars(str.substring(1));
		}
	}

}
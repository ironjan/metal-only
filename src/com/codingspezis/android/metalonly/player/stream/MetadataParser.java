package com.codingspezis.android.metalonly.player.stream;

import java.util.*;

import com.codingspezis.android.metalonly.player.favorites.*;

/**
 * 
 * parses interpret title genre and moderator from meta data
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

		long date = Calendar.getInstance().getTimeInMillis();

		MODERATOR.replace(" OnAir", "");

		Song song = new Song(INTERPRET, TITLE, MODERATOR, date);

		return song;
	}

	/**
	 * checks string str for occurrence of '*'
	 * 
	 * @param toCount
	 *            string to check
	 * @return number of char '*' containing in str
	 */
	private static int numberOfStars(String toCount) {
		final String withoutStars = toCount.replaceAll("\\*", "");

		final int lengthWithStars = toCount.length();
		final int lengthWithoutStars = withoutStars.length();
		return lengthWithStars - lengthWithoutStars;

	}

}
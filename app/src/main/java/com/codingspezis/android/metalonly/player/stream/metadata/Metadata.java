package com.codingspezis.android.metalonly.player.stream.metadata;

import com.codingspezis.android.metalonly.player.favorites.Song;

import java.util.Calendar;

/**
 * parses interpret title getGenre and getModerator from meta data
 */
public class Metadata {

    public static final int REQUIRED_NUMBER_OF_STARS = 3;
    public static final int MODERATOR_SLICE = 1;
    public static final int GENRE_SLICE = 2;
    public static final String DEFAULT_MODEDRATOR = "MetalHead OnAir";
    public static final String DEFAULT_GENRE = "Mixed Metal";
    public static final Metadata DEFAULT_METADATA = new Metadata("", "", "", "");

    private String interpret = "";
    private String title = "";
    private String genre = "";
    private String moderator = "";

    public Metadata(String moderator, String genre, String interpret, String title) {
        this.moderator = moderator;
        this.genre = genre;
        this.interpret = interpret;
        this.title = title;
    }

    /**
     * Parses the given data string into a Metadata object
     *
     * @param data the string to be parsed
     * @return a new Metadata object. Silently returns default object if share goes wrong
     */
    public static Metadata fromString(String data) {
        final String genre, moderator, interpret, title;
        try {
            if (numberOfStars(data) >= REQUIRED_NUMBER_OF_STARS) {
                String[] slices = data.split("\\*");
                genre = slices[GENRE_SLICE].trim();
                moderator = slices[MODERATOR_SLICE].trim();
                data = slices[0].trim();
            } else {
                moderator = DEFAULT_MODEDRATOR;
                genre = DEFAULT_GENRE;
            }
            interpret = data.substring(0, data.indexOf(" - ")).trim();
            title = data.substring(data.indexOf(" - ") + 2).trim();
            return new Metadata(moderator, genre, interpret, title);

        } catch (Exception e) {
            return DEFAULT_METADATA;
        }

    }

    /**
     * checks string str for occurrence of '*'
     *
     * @param toCount string to check
     * @return number of char '*' containing in str
     */
    private static int numberOfStars(String toCount) {
        final String withoutStars = toCount.replaceAll("\\*", "");

        final int lengthWithStars = toCount.length();
        final int lengthWithoutStars = withoutStars.length();

        final int result = lengthWithStars - lengthWithoutStars;

        return result;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getModerator() {
        return moderator;
    }

    public Song toSong() {
        long date = Calendar.getInstance().getTimeInMillis();

        Song song = new Song(interpret, title, moderator, date);

        return song;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "interpret='" + interpret + '\'' +
                ", title='" + title + '\'' +
                ", getGenre='" + genre + '\'' +
                ", getModerator='" + moderator + '\'' +
                '}';
    }
}
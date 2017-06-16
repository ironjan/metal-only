package com.codingspezis.android.metalonly.player.stream.metadata;

import com.codingspezis.android.metalonly.player.core.Song;

import java.util.Calendar;

/**
 * parses interpret title getGenre and getModerator from meta data
 */
public class Metadata {

    static final String DEFAULT_MODERATOR = "MetalHead OnAir";
    static final String DEFAULT_GENRE = "Mixed Metal";
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
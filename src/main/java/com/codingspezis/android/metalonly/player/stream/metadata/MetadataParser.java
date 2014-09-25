package com.codingspezis.android.metalonly.player.stream.metadata;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.favorites.*;

import org.slf4j.*;

import java.util.*;

/**
 * parses interpret title genre and moderator from meta data
 */
public class MetadataParser {

    public static final int REQUIRED_NUMBER_OF_STARS = 3;
    public static final int MODERATOR_SLICE = 1;
    public static final int GENRE_SLICE = 2;
    public static final String DEFAULT_MODEDRATOR = "MetalHead OnAir";
    public static final String DEFAULT_GENRE = "Mixed Metal";
    private String interpret = "";
    private String title = "";
    private String genre = "";
    private String moderator = "";

    private static final String TAG = MetadataParser.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(TAG);

    public MetadataParser(String data) {
        try {
            if (numberOfStars(data) >= REQUIRED_NUMBER_OF_STARS) {
                // TODO which exceptions can be thrown? do we need catch
                String[] slices = data.split("\\*");
                genre = slices[GENRE_SLICE].trim();
                moderator = slices[MODERATOR_SLICE].trim();
                data = slices[0].trim();
            } else {
                moderator = DEFAULT_MODEDRATOR;
                genre = DEFAULT_GENRE;
            }
            interpret = extractInterpret(data);
            title = extractTitle(data);
        } catch (Exception e) {
            moderator = "";
            genre = "";
            interpret = "";
            title = "";
        }

        if (BuildConfig.DEBUG) LOGGER.debug("Created: {} from MetadataParser({})", this, data);
    }

    private String extractTitle(String data) {
        return data.substring(data.indexOf(" - ") + 2).trim();
    }

    private String extractInterpret(String data) {
        return data.substring(0, data.indexOf(" - ")).trim();
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
        if (BuildConfig.DEBUG) LOGGER.debug("toSong()");
        long date = Calendar.getInstance().getTimeInMillis();

        moderator.replace(" OnAir", "");

        Song song = new Song(interpret, title, moderator, date);

        if (BuildConfig.DEBUG) LOGGER.debug("toSong() -> {}", song);
        return song;
    }

    /**
     * checks string str for occurrence of '*'
     *
     * @param toCount string to check
     * @return number of char '*' containing in str
     */
    private static int numberOfStars(String toCount) {
        if (BuildConfig.DEBUG) LOGGER.debug("numberOfStars({})", toCount);

        final String withoutStars = toCount.replaceAll("\\*", "");

        final int lengthWithStars = toCount.length();
        final int lengthWithoutStars = withoutStars.length();

        final int result = lengthWithStars - lengthWithoutStars;
        if (BuildConfig.DEBUG) LOGGER.debug("numberOfStars({}) -> ", toCount, result);

        return result;

    }

    @Override
    public String toString() {
        return "MetadataParser{" +
                "interpret='" + interpret + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", moderator='" + moderator + '\'' +
                '}';
    }
}
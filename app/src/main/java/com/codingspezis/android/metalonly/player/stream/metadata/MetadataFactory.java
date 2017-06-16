package com.codingspezis.android.metalonly.player.stream.metadata;

/**
 * Factory class to build {@link Metadata} from {@link String}s.
 */
public class MetadataFactory {
    private static final int REQUIRED_NUMBER_OF_STARS = 3;
    private static final int MODERATOR_SLICE = 1;
    private static final int GENRE_SLICE = 2;

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
                moderator = Metadata.DEFAULT_MODERATOR;
                genre = Metadata.DEFAULT_GENRE;
            }
            interpret = data.substring(0, data.indexOf(" - ")).trim();
            title = data.substring(data.indexOf(" - ") + 2).trim();
            return new Metadata(moderator, genre, interpret, title);

        } catch (Exception e) {
            return Metadata.DEFAULT_METADATA;
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
}

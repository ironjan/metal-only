package com.github.ironjan.metalonly.client_library;

import com.codingspezis.android.metalonly.player.core.ExtendedShowInformation;
import com.codingspezis.android.metalonly.player.core.Track;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * {
 *     "moderated": false,
 *     "getModerator": "MetalHead",
 *     "sendung": "Keine Gruesse und Wuensche moeglich. (Mixed Metal)",
 *     "wunschvoll": "1",
 *     "grussvoll": "1",
 *     "wunschlimit": "0",
 *     "grusslimit": "0"
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats implements ExtendedShowInformation{
    private static final String WISH_GREET_FULL = "1";

    private String moderator = "Unbekannt";
    private String sendung = "Ladefehler";
    private boolean canWish;
    private boolean canGreet;
    private boolean moderated;
    private Track track;

    private int wishLimit = 0;
    private int greetingLimit = 0;
    private String genre = "Metal";

    public boolean isCanWish() {
        return canWish;
    }

    public boolean isCanGreet() {
        return canGreet;
    }

    /**
     * Stats(moderator = "Unbekannt",
     *       sendung = "Ladefehler",
     *       canWish = false,
     *       canGreet = false,
     *       moderated = false,
     *       track = null,
     *       wishLimit = 0,
     *       greetingLimit = 0,
     *       )
     * @return Stats with values as desribed above.
     */
    public static Stats getDefault() {
        return new Stats();
    }

    @JsonProperty("sendung")
    public void setSendung(String sendung) {
        this.sendung = sendung;
        genre = GenreExtractor.INSTANCE.extract(this.sendung);
    }

    @JsonProperty("wunschvoll")
    public void setCanWish(String wunschvollString) {
        canWish = !(WISH_GREET_FULL.equals(wunschvollString));
    }

    public int getWishLimit() {
        return wishLimit;
    }

    @JsonProperty("grussvoll")
    public void setCanGreet(String grussvoll) {
        canGreet = !(WISH_GREET_FULL.equals(grussvoll));
    }

    @JsonProperty("wunschlimit")
    public void setWishLimit(String wishLimit) {
        try {
            this.wishLimit = Integer.parseInt(wishLimit);
        } catch (NumberFormatException e) {
            this.wishLimit = 0;
        }
    }

    @JsonProperty("grusslimit")
    public void setGreetingLimit(String greetingLimit) {
        try {
            this.greetingLimit = Integer.parseInt(greetingLimit);
        } catch (NumberFormatException e) {
            this.greetingLimit = 0;
        }
    }

    @JsonProperty("moderated")
    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    public String getModerator() {
        return moderator;
    }

    @JsonProperty("moderator")
    public void setModerator(String moderator) {
        this.moderator = moderator;
    }

    public String getGenre() {
        if (genre == null) {
            genre = GenreExtractor.INSTANCE.extract(sendung);
        }
        return genre;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(SimpleTrack track) {
        this.track = track;
    }


    public boolean isNotModerated(){
        return !moderated;
    }

    @Override
    public boolean getCanNeitherWishNorGreet() {
        return !(canWish || canGreet);
    }

    @NotNull
    @Override
    public String getShowTitle() {
        return sendung;
    }

    @Override
    public boolean getCanWish() {
        return canWish;
    }

    @Override
    public boolean getCanGreet() {
        return canGreet;
    }

    @Override
    public int getGreetLimit() {
        return greetingLimit;
    }
}

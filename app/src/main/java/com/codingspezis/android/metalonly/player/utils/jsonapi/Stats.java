package com.codingspezis.android.metalonly.player.utils.jsonapi;

import com.codingspezis.android.metalonly.player.core.BasicShowInformation;
import com.codingspezis.android.metalonly.player.core.WishAndGreetConstraints;
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
public class Stats implements BasicShowInformation, WishAndGreetConstraints{
    private static final String WISH_GREET_FULL = "1";

    private String moderator = "Unbekannt";
    private String sendung = "Ladefehler";
    private boolean canWish;
    private boolean canGreet;
    private boolean moderated;

    public boolean isCanWish() {
        return canWish;
    }

    public boolean isCanGreet() {
        return canGreet;
    }

    public boolean isModerated() {
        return moderated;
    }


    private int wishLimit = 0;
    private int greetingLimit = 0;
    private String genre = "Metal";

    public static Stats getDefault() {
        return new Stats();
    }

    @JsonProperty("sendung")
    public void setSendung(String sendung) {
        this.sendung = sendung;
        updateGenre();
    }

    @JsonProperty("wunschvoll")
    public void setCanWish(String wunschvollString) {
        this.canWish = !(WISH_GREET_FULL.equals(wunschvollString));
    }

    public int getWishLimit() {
        return wishLimit;
    }

    @JsonProperty("grussvoll")
    public void setCanGreet(String grussvoll) {
        this.canGreet = !(WISH_GREET_FULL.equals(grussvoll));
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
            updateGenre();
        }
        return genre;
    }

    private void updateGenre() {
        int positionOfOpeningParenthesis = sendung.indexOf("(");
        int positionOfClosingParenthesis = sendung.indexOf(")");
        int startOfGenreName = positionOfOpeningParenthesis + 1;
        int lengthOfGenre = positionOfClosingParenthesis - startOfGenreName;


        boolean hasNoGenre = (positionOfOpeningParenthesis == -1
                || positionOfClosingParenthesis == -1
                || lengthOfGenre <= 0);

        this.genre = (hasNoGenre)
                ? ""
                : sendung.substring(startOfGenreName, positionOfClosingParenthesis);
    }


    public boolean isNotModerated(){
        return !moderated;
    }

    public boolean canNeitherWishNorGreet() {
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

package com.github.ironjan.metalonly.client_library;

public class RetrofitStats {
    String moderator;
    String sendung;
    boolean canWish;
    boolean canGreet;
    boolean moderated;
    int wishLimit;
    int greetingLimit;

    public String getModerator() {
        return this.moderator;
    }

    public void setModerator(String moderator) {
        this.moderator = moderator;
    }

    public String getSendung() {
        return this.sendung;
    }

    public void setSendung(String sendung) {
        this.sendung = sendung;
    }

    public boolean isCanWish() {
        return this.canWish;
    }

    public void setCanWish(boolean canWish) {
        this.canWish = canWish;
    }

    public boolean isCanGreet() {
        return this.canGreet;
    }

    public void setCanGreet(boolean canGreet) {
        this.canGreet = canGreet;
    }

    public boolean isModerated() {
        return this.moderated;
    }

    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    public int getWishLimit() {
        return this.wishLimit;
    }

    public void setWishLimit(int wishLimit) {
        this.wishLimit = wishLimit;
    }

    public int getGreetingLimit() {
        return this.greetingLimit;
    }

    public void setGreetingLimit(int greetingLimit) {
        this.greetingLimit = greetingLimit;
    }

    public RetrofitStats() {

    }

    @Override
    public String toString() {
        return "RetrofitStats{" +
                "moderator='" + moderator + '\'' +
                ", sendung='" + sendung + '\'' +
                ", canWish=" + canWish +
                ", canGreet=" + canGreet +
                ", moderated=" + moderated +
                ", wishLimit=" + wishLimit +
                ", greetingLimit=" + greetingLimit +
                '}';
    }
}

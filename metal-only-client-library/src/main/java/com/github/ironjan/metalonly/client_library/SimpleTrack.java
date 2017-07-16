package com.github.ironjan.metalonly.client_library;

import com.codingspezis.android.metalonly.player.core.Track;

import org.jetbrains.annotations.NotNull;

public class SimpleTrack implements Track {
    private String artist;
    private String title;

    @Override
    public String getArtist() {
        return (artist != null) ? artist: "";
    }

    @NotNull
    @Override
    public String getTitle() {
        return (title != null) ? title: "";
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

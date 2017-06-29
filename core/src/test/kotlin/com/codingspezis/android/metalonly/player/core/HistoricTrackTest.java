package com.codingspezis.android.metalonly.player.core;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HistoricTrackTest {
    @Test
    public void testSongWithOutOnAirStaysUnchanged() {
        HistoricTrack track = new HistoricTrack("interpret", "title", "thumb", 0L);
    	Assert.assertThat(track.getTitle(), CoreMatchers.is(CoreMatchers.equalTo("title")));
    	Assert.assertThat(track.getArtist(), CoreMatchers.is(CoreMatchers.equalTo("interpret")));
    	Assert.assertThat(track.getModerator(), CoreMatchers.is(CoreMatchers.equalTo("thumb")));
    	Assert.assertThat(track.getPlayedAtAsLong(), CoreMatchers.is(CoreMatchers.equalTo(0L)));
    }

    @Test
    public void testSongWithOnAirRemovedOnAir() {
        HistoricTrack track = new HistoricTrack("interpret", "title", "MetalHead OnAir", 0L);
    	Assert.assertThat(track.getTitle(), CoreMatchers.is(CoreMatchers.equalTo("title")));
    	Assert.assertThat(track.getArtist(), CoreMatchers.is(CoreMatchers.equalTo("interpret")));
    	Assert.assertThat(track.getModerator(), CoreMatchers.is(CoreMatchers.equalTo("MetalHead")));
    	Assert.assertThat(track.getPlayedAtAsLong(), CoreMatchers.is(CoreMatchers.equalTo(0L)));
    }
}
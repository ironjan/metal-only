package com.codingspezis.android.metalonly.player.favorites;

import com.codingspezis.android.metalonly.player.core.HistoricTrack;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HistoricTrackTest {
    @Test
    public void testSongWithOutOnAirStaysUnchanged() {
        HistoricTrack track = new HistoricTrack("interpret", "title", "thumb", 0L);
    	assertThat(track.getTitle(), is(equalTo("title")));
    	assertThat(track.getArtist(), is(equalTo("interpret")));
    	assertThat(track.getModerator(), is(equalTo("thumb")));
    	assertThat(track.getPlayedAtAsLong(), is(equalTo(0L)));
    }

    @Test
    public void testSongWithOnAirRemovedOnAir() {
        HistoricTrack track = new HistoricTrack("interpret", "title", "MetalHead OnAir", 0L);
    	assertThat(track.getTitle(), is(equalTo("title")));
    	assertThat(track.getArtist(), is(equalTo("interpret")));
    	assertThat(track.getModerator(), is(equalTo("MetalHead")));
    	assertThat(track.getPlayedAtAsLong(), is(equalTo(0L)));
    }
}
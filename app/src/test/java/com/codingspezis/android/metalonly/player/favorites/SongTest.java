package com.codingspezis.android.metalonly.player.favorites;

import com.codingspezis.android.metalonly.player.core.Song;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SongTest {
    @Test
    public void testSongWithOutOnAirStaysUnchanged() {
        Song song = new Song("interpret", "title", "thumb", 0L);
    	assertThat(song.getTitle(), is(equalTo("title")));
    	assertThat(song.getArtist(), is(equalTo("interpret")));
    	assertThat(song.getModerator(), is(equalTo("thumb")));
    	assertThat(song.getPlayedAtAsLong(), is(equalTo(0L)));
    }

    @Test
    public void testSongWithOnAirRemovedOnAir() {
        Song song = new Song("interpret", "title", "MetalHead OnAir", 0L);
    	assertThat(song.getTitle(), is(equalTo("title")));
    	assertThat(song.getArtist(), is(equalTo("interpret")));
    	assertThat(song.getModerator(), is(equalTo("MetalHead")));
    	assertThat(song.getPlayedAtAsLong(), is(equalTo(0L)));
    }
}
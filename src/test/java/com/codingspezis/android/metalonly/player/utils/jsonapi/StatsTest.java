package com.codingspezis.android.metalonly.player.utils.jsonapi;

import junit.framework.Assert;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.lang.IllegalArgumentException;

import dalvik.annotation.TestTarget;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class StatsTest {

    Stats stats;

    @Before
    public void initStats() {
        stats = new Stats();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_EmptyStringThrowsException() {
        stats.setSendung("");
        String genre = stats.getGenre();
        Assert.assertEquals("Empty string had a genre!", "", genre);
    }
    @Test
    public void test_EmptyParenthesesHaveNoGenre() {
        stats.setSendung("Sendung ()");
        String genre = stats.getGenre();
        Assert.assertEquals("Empty Parentheses had a genre!", "", genre);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_OnlyOpeningParenthesisThrowsException() {
        stats.setSendung("Sendung (");
        String genre = stats.getGenre();
    }

    @Test
    public void test_OnlyClosingParenthesisHasWrongGenre() {
        stats.setSendung("Sendung )");
        String genre = stats.getGenre();
        Assert.assertEquals("Got the wrong (wrong genre)","Sendung ",genre);
    }

    @Test
    public void test_OnlyParenthesesHaveNoGenre() {
        stats.setSendung("()");
        String genre = stats.getGenre();
        Assert.assertEquals("Only Parentheses had a genre!", "", genre);
    }

}
package com.codingspezis.android.metalonly.player.utils.jsonapi;

import junit.framework.TestCase;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.RobolectricTestRunner;

@Config(sdk = Build.VERSION_CODES.JELLY_BEAN_MR2)
@RunWith(RobolectricTestRunner.class)
public class StatsTest extends TestCase {

    public static final String EMPTY_STRING = "";
    public static final String EMPTY_PARENTHESES = "Sendung ()";
    public static final String ONLY_OPENING_PARENTHESIS = "Sendung (";
    public static final String ONLY_CLOSING_PARENTHESIS = "Sendung )";
    public static final String ONLY_PARENTHESES = "()";
    public static final String NO_PARENTHESES = "Sendung";
    Stats stats;

    @Before
    public void initStats() {
        stats = Stats.getDefault();
    }

    @Test
    public void test_EmptyStringShouldHaveNoGenre() {
        stats.setSendung(EMPTY_STRING);
        String genre = stats.getGenre();
        Assert.assertEquals("Genre was not empty when setting empty string.", EMPTY_STRING, genre);
    }

    @Test
    public void test_EmptyParenthesesShouldHaveNoGenre() {
        stats.setSendung(EMPTY_PARENTHESES);
        String genre = stats.getGenre();
        Assert.assertEquals("Genre was not empty when setting setting empty parentheses.", EMPTY_STRING, genre);
    }

    @Test
    public void test_OnlyOpeningParenthesesShouldHaveNoGenre() {
        stats.setSendung(ONLY_OPENING_PARENTHESIS);
        String genre = stats.getGenre();
        Assert.assertEquals("Genre was not empty when setting setting string with only opening parentheses.", EMPTY_STRING, genre);
    }

    @Test
    public void test_OnlyClosingParenthesesShouldHaveNoGenre() {
        stats.setSendung(ONLY_CLOSING_PARENTHESIS);
        String genre = stats.getGenre();
        Assert.assertEquals("Genre was not empty when setting string with only closing parentheses.", EMPTY_STRING, genre);
    }

    @Test
    public void test_OnlyParenthesesShouldHaveNoGenre() {
        stats.setSendung(ONLY_PARENTHESES);
        String genre = stats.getGenre();
        Assert.assertEquals("Genre was not empty when setting string with only parentheses.", EMPTY_STRING, genre);
    }

    @Test
    public void test_ParenthesesWithGenreShouldHaveGenre() {
        stats.setSendung("(Genre)");
        String genre = stats.getGenre();
        Assert.assertEquals("Genre", genre);
    }

    @Test
    public void test_FullShowInfoShouldHaveGenre() {
        stats.setSendung("Sendung (Genre)");
        String genre = stats.getGenre();
        Assert.assertEquals("Genre", genre);
    }

    @Test
    public void test_StringWithOnlySendungShouldHaveNoGenre() {
        stats.setSendung(NO_PARENTHESES);
        String genre = stats.getGenre();
        Assert.assertEquals(EMPTY_STRING, genre);
    }

}
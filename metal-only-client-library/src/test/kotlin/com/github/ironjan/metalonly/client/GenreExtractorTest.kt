package com.github.ironjan.metalonly.client

import junit.framework.Assert
import org.junit.Test

class GenreExtractorTest {
    val genre = "Mixed Rock & Metal"

    @Test
    fun extractsCorrectlyFormedGenreTest() {
        Assert.assertEquals(genre, GenreExtractor.extract("showTitle ($genre)"))
    }

    /** Regression test for [https://github.com/ironjan/metal-only/issues/41] */
    @Test
    fun extractsGenreWithMissingEndParenthesis() {
        Assert.assertEquals(genre, GenreExtractor.extract("showTitle ($genre"))
    }

    @Test
    fun extractsNothingWhenGenreIsMissing() {
        Assert.assertEquals("", GenreExtractor.extract("showTitle"))
    }

    @Test
    fun extractsLastInfoWhenShowTitleHasMultipleParenthesesPairs() {
        Assert.assertEquals(genre, GenreExtractor.extract("title (wrong) ($genre)"))
    }
}

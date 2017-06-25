package com.codingspezis.android.metalonly.player.utils

internal object GenreExtractor {
    fun extract(showTitle: String): String {
        val startOfGenreName = showTitle.lastIndexOf("(") + 1

        val indexOfClosingPar = showTitle.lastIndexOf(")")
        val endOfGenreName = if (indexOfClosingPar == -1) showTitle.length else indexOfClosingPar

        val lengthOfGenre = endOfGenreName - startOfGenreName

        val hasNoGenre = startOfGenreName == 0
                || lengthOfGenre <= 0

        return if (hasNoGenre)
            ""
        else
            showTitle.substring(startOfGenreName, endOfGenreName)
    }
}

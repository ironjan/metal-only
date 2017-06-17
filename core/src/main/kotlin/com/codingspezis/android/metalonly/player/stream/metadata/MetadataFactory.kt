package com.codingspezis.android.metalonly.player.stream.metadata

/**
 * Factory class to build [Metadata] from [String]s.
 */
object MetadataFactory {
    val DEFAULT_METADATA = createMetadata("", "", "", "")
    private val DEFAULT_MODERATOR = "MetalHead OnAir"
    private val DEFAULT_GENRE = "Mixed Metal"

    private val REQUIRED_NUMBER_OF_STARS = 3
    private val MODERATOR_SLICE = 1
    private val GENRE_SLICE = 2

    /**
     * Parses the given data string into a Metadata object

     * @param data the string to be parsed
     * *
     * @return a new Metadata object. Silently returns default object if share goes wrong
     */
    fun createFromString(data: String): Metadata {
        var data = data
        try {
            val genre: String
            val moderator: String
            if (numberOfStars(data) >= REQUIRED_NUMBER_OF_STARS) {
                val slices = data.split("\\*".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                genre = slices[GENRE_SLICE].trim { it <= ' ' }
                moderator = slices[MODERATOR_SLICE].trim { it <= ' ' }
                data = slices[0].trim { it <= ' ' }
            } else {
                moderator = DEFAULT_MODERATOR
                genre = DEFAULT_GENRE
            }
            val interpret: String = data.substring(0, data.indexOf(" - ")).trim { it <= ' ' }
            val title: String = data.substring(data.indexOf(" - ") + 2).trim { it <= ' ' }
            return createMetadata(moderator, genre, interpret, title)

        } catch (e: Exception) {
            return DEFAULT_METADATA
        }

    }

    /**
     * checks string str for occurrence of '*'

     * @param toCount string to check
     * *
     * @return number of char '*' containing in str
     */
    private fun numberOfStars(toCount: String): Int {
        val withoutStars = toCount.replace("\\*".toRegex(), "")

        val lengthWithStars = toCount.length
        val lengthWithoutStars = withoutStars.length

        val result = lengthWithStars - lengthWithoutStars

        return result
    }

    fun createMetadata(moderator: String, genre: String, interpret: String, title: String): Metadata {
        return Metadata(moderator, genre, interpret, title)
    }
}

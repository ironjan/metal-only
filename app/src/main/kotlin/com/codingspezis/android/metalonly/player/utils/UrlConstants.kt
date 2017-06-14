package com.codingspezis.android.metalonly.player.utils

/**
 * Centralized class for all URL constants
 */
object UrlConstants {
    val METAL_ONLY_DONATION_URL = "https://www.metal-only.de/?action=donation"
    val YOUTUBE_SEARCH_URL = "https://www.youtube.com/results?search_query="

    /**
     * Note that the Stream is the only non-https url!
     */
    val STREAM_URL_128 = "http://server1.blitz-stream.de:4400"

    /**
     * Old plan loading URL.
     * @deprecated Use JSON API instead
     */
    val API_OLD_PLAN_URL = "https://www.metal-only.de/botcon/mob.php?action=plan"

    val METAL_ONLY_MODERATOR_PIC_BASE_URL = "https://www.metal-only.de/botcon/mob.php?action=pic&nick="
}

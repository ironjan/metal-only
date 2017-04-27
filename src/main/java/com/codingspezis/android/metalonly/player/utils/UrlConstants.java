package com.codingspezis.android.metalonly.player.utils;

/**
 * Centralized class for all URL constants
 */
public final class UrlConstants {
    public static final String METAL_ONLY_DONATION_URL = "https://www.metal-only.de/?action=donation";
    public static final String YOUTUBE_SEARCH_URL = "https://www.youtube.com/results?search_query=";

    public static final String METAL_ONLY_WUNSCHSCRIPT_POST_URL = "https://www.metal-only.de/?action=wunschscript&do=save";

    /**
     * Note that the Stream is the only non-https url!
     */
    public static final String STREAM_URL_128 = "http://server1.blitz-stream.de:4400";

    public static final String METAL_ONLY_API_BASE_URL = "https://www.metal-only.de/botcon/mob.php?action=";
    public static final String API_OLD_PLAN_URL = "https://www.metal-only.de/botcon/mob.php?action=plan";
    public static final String API_STATS_PATH = "stats";
    public static final String API_PLAN_PATH = "plannew";
    public static final String API_PLAN_WITH_STATS_PATH = "all";
    public static final String METAL_ONLY_MODERATOR_PIC_BASE_URL = "https://www.metal-only.de/botcon/mob.php?action=pic&nick=";
}

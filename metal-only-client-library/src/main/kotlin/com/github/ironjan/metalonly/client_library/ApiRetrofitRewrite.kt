package com.github.ironjan.metalonly.client_library


import retrofit2.Call
import retrofit2.http.GET

interface ApiRetrofitRewrite {

    /**
     * Requests the show's stats

     * @return the show's stats. May be null when errors occur.
     * *
     * @throws NoInternetException if no internet connection is present
     */
    @get:GET("mob.php?action="+BuildConfig.API_STATS_PATH)
    val stats: Call<Stats>

    /**
     * Requests this week's sending plan

     * @return this week's sending plan. May be null when errors occur.
     * *
     * @throws NoInternetException if no internet connection is present
     */
    @get:GET("mob.php?action="+BuildConfig.API_PLAN_PATH)
    val plan: Call<Plan>

    /**
     * Requests this week's sending plan including stats

     * @return this week's sending plan including stats. May be null when errors occur.
     * *
     * @throws NoInternetException if no internet connection is present
     */
    @get:GET("mob.php?action="+BuildConfig.API_PLAN_WITH_STATS_PATH)
    val planWithStats: Call<PlanWithStats>


    /**
     * Gets the current track via API

     * @return the currently played track. May be null when errors occur.
     * *
     * @throws NoInternetException if no internet connection is present
     */
    @get:GET("mob.php?action="+"track")
    val track: Call<TrackWrapper>

}

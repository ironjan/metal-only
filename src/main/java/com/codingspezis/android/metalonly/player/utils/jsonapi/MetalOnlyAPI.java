package com.codingspezis.android.metalonly.player.utils.jsonapi;

import org.androidannotations.annotations.rest.*;
import org.androidannotations.api.rest.*;
import org.springframework.http.converter.json.*;

/**
 * Interface to generate the REST-Client
 */
@Rest(converters = MappingJackson2HttpMessageConverter.class, rootUrl = "https://www.metal-only.de/botcon/mob.php?action=")
interface MetalOnlyAPI extends RestClientErrorHandling, RestClientSupport {

    /**
     * Requests the show's stats
     *
     * @return the show's stats. Will not be null.
     */
    @Get("stats")
    Stats getStats();

    /**
     * Requests this week's sending plan
     *
     * @return this week's sending plan. Will not be null.
     */
    @Get("plannew")
    Plan getPlan();

    /**
     * Requests this week's sending plan including stats
     *
     * @return this week's sending plan including stats. Will not be null.
     */
    @Get("all")
    PlanWithStats getPlanWithStats();

}

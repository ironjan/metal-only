package com.codingspezis.android.metalonly.player.utils.jsonapi;

import com.codingspezis.android.metalonly.player.utils.UrlConstants;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Interface to generate the REST-Client
 */
@Rest(converters = MappingJackson2HttpMessageConverter.class, rootUrl = UrlConstants.METAL_ONLY_API_BASE_URL)
interface MetalOnlyAPI extends RestClientErrorHandling, RestClientSupport {

    /**
     * Requests the show's stats
     *
     * @return the show's stats. Will not be null.
     */
    @Get(UrlConstants.API_STATS_PATH)
    Stats getStats();

    /**
     * Requests this week's sending plan
     *
     * @return this week's sending plan. Will not be null.
     */
    @Get(UrlConstants.API_PLAN_PATH)
    Plan getPlan();

    /**
     * Requests this week's sending plan including stats
     *
     * @return this week's sending plan including stats. Will not be null.
     */
    @Get(UrlConstants.API_PLAN_WITH_STATS_PATH)
    PlanWithStats getPlanWithStats();

}

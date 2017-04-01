package com.codingspezis.android.metalonly.player.utils.jsonapi;

import com.codingspezis.android.metalonly.player.utils.UrlConstants;

import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.androidannotations.rest.spring.api.RestClientSupport;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;

/**
 * Interface to generate the REST-Client
 */
@Rest(converters = {MappingJackson2HttpMessageConverter.class }, rootUrl = UrlConstants.METAL_ONLY_API_BASE_URL)
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

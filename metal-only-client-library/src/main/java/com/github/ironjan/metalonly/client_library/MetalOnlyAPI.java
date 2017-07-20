package com.github.ironjan.metalonly.client_library;

import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.androidannotations.rest.spring.api.RestClientSupport;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

/**
 * Interface to generate the REST-Client via https://github.com/androidannotations/androidannotations/
 */
@Rest(converters = {MappingJackson2HttpMessageConverter.class }, rootUrl = BuildConfig.METAL_ONLY_API_BASE_URL)
interface MetalOnlyAPI extends RestClientErrorHandling, RestClientSupport {

    /**
     * Requests the show's stats
     *
     * @return the show's stats. May be null when errors occur.
     *
     * @throws NoInternetException if no internet connection is present
     *
     * @throws RestClientException rethrow of underlying API implementation exception
     *
     * @throws ResourceAccessException if there are network problems
     */
    @Get(BuildConfig.API_STATS_PATH)
    Stats getStats();

    /**
     * Requests this week's sending plan
     *
     * @return this week's sending plan. May be null when errors occur.
     *
     * @throws NoInternetException if no internet connection is present
     *
     * @throws RestClientException rethrow of underlying API implementation exception
     *
     * @throws ResourceAccessException if there are network problems
     */
    @Get(BuildConfig.API_PLAN_PATH)
    Plan getPlan();

    /**
     * Requests this week's sending plan including stats
     *
     * @return this week's sending plan including stats. May be null when errors occur.
     *
     * @throws NoInternetException if no internet connection is present
     *
     * @throws RestClientException rethrow of underlying API implementation exception
     *
     * @throws ResourceAccessException if there are network problems
     */
    @Get(BuildConfig.API_PLAN_WITH_STATS_PATH)
    PlanWithStats getPlanWithStats();


    /**
     * Gets the current track via API
     *
     * @return the currently played track. May be null when errors occur.
     *
     * @throws NoInternetException if no internet connection is present
     *
     * @throws RestClientException rethrow of underlying API implementation exception
     *
     * @throws ResourceAccessException if there are network problems
     */
    @Get("track")
    TrackWrapper getTrack();
}

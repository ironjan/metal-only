package com.github.ironjan.metalonly.client_library;

import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.androidannotations.rest.spring.api.RestClientSupport;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;

/**
 * Interface to generate the REST-Client.
 *
 * This interface should be internal, as should be the generated code. Interaction with the client
 * should only be done via interfaces defined in core.
 * TODO Wrap the API code behind a delegator that only uses core interfaces
 */
@Rest(converters = {MappingJackson2HttpMessageConverter.class }, rootUrl = BuildConfig.METAL_ONLY_API_BASE_URL)
public interface MetalOnlyAPI extends RestClientErrorHandling, RestClientSupport {

    /**
     * Requests the show's stats
     *
     * @return the show's stats. May be null when errors occur.
     */
    @Get(BuildConfig.API_STATS_PATH)
    Stats getStats();

    /**
     * Requests this week's sending plan
     *
     * @return this week's sending plan. May be null when errors occur.
     */
    @Get(BuildConfig.API_PLAN_PATH)
    Plan getPlan();

    /**
     * Requests this week's sending plan including stats
     *
     * @return this week's sending plan including stats. May be null when errors occur.
     */
    @Get(BuildConfig.API_PLAN_WITH_STATS_PATH)
    PlanWithStats getPlanWithStats();


    /**
     * Gets the current track via API
     * @return the currently played track. May be null when errors occur.
     * @throws RestClientException when a REST related exception occurs
     * @throws NoInternetException when this method is called without internet connection
     */
    @Get("track")
    TrackWrapper getTrack();
}

package com.codingspezis.android.metalonly.player.utils.jsonapi;

import org.androidannotations.annotations.rest.*;
import org.androidannotations.api.rest.*;
import org.springframework.http.converter.json.*;

@Rest(converters = MappingJackson2HttpMessageConverter.class, rootUrl = "http://metal-only.de/botcon/mob.php?action=")
interface MetalOnlyAPI extends RestClientErrorHandling, RestClientSupport {

    @Get("stats")
    @Accept(MediaType.APPLICATION_JSON)
    Stats getStats();

    @Get("plannew")
    @Accept(MediaType.APPLICATION_JSON)
    Plan getPlan();

    @Get("all")
    @Accept(MediaType.APPLICATION_JSON)
    PlanWithStats getPlanWithStats();

}

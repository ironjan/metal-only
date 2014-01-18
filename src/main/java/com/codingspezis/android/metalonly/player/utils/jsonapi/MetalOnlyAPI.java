package com.codingspezis.android.metalonly.player.utils.jsonapi;

import org.springframework.http.converter.json.*;
import org.springframework.web.client.*;

import org.androidannotations.annotations.rest.*;

@Rest(converters = MappingJackson2HttpMessageConverter.class, rootUrl = "http://metal-only.de/botcon/mob.php?action=")
interface MetalOnlyAPI {

	@Get("stats")
	Stats getStats();

	@Get("plannew")
	Plan getPlan();

	@Get("all")
	PlanWithStats getPlanWithStats();

	RestTemplate getRestTemplate();

	void setRestTemplate(RestTemplate restTemplate);
}

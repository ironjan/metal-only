package com.github.ironjan.metalonly.client_library;

import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

/**
 * Interface to generate the REST-Client for wishes and greetings
 */
@Rest(converters = {FormHttpMessageConverter.class, StringHttpMessageConverter.class}, rootUrl = BuildConfig.METAL_ONLY_API_BASE_URL)
public interface WishGreetAPI {

    /**
     * Submits a wish with greetings
     * @param nick the user's nick
     * @param artist the wished artist
     * @param song the wished song
     * @param greet some greetings
     */
    @Post(BuildConfig.METAL_ONLY_WUNSCHSCRIPT_POST_URL)
    String postWishAndGreetings(@Field String nick, @Field String artist, @Field String song, @Field String greet);

    /**
     * Submits some greetings
     * @param nick the user's nick
     * @param greet some greetings
     */
    @Post(BuildConfig.METAL_ONLY_WUNSCHSCRIPT_POST_URL)
    String postGreetings(@Field String nick, @Field String greet);
}

package com.codingspezis.android.metalonly.player.utils.jsonapi;

import org.androidannotations.annotations.*;
import org.androidannotations.api.rest.*;
import org.springframework.web.client.*;

@EBean
public class MyErrorHandler implements RestErrorHandler {
    @Override
    public void onRestClientExceptionThrown(RestClientException e) {

    }
}

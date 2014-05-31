package com.codingspezis.android.metalonly.player.stream.exceptions;

import com.spoledge.aacdecoder.*;

/**
 * Thrown
 */
@SuppressWarnings("serial")
public class WrongSampleRateException extends PlayerException{

    private int rate;

    public WrongSampleRateException(int rate) {
        this.rate = rate;
    }

    @Override
    public String getMessage() {
        return "Wrong sample rate detected: " + rate;
    }

}

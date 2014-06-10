package com.codingspezis.android.metalonly.player.stream.exceptions;

import com.spoledge.aacdecoder.*;

/**
 * Thrown
 */
@SuppressWarnings("serial")
public class WrongSampleRateException extends PlayerException{


    public WrongSampleRateException(int rate) {
        super("Wrong sample rate detected: " + rate);
    }

}

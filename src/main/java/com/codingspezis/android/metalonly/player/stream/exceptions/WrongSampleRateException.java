package com.codingspezis.android.metalonly.player.stream.exceptions;

/**
 * Thrown
 */
@SuppressWarnings("serial")
public class WrongSampleRateException extends Exception {


    public WrongSampleRateException(int rate) {
        super("Wrong sample rate detected: " + rate);
    }

}

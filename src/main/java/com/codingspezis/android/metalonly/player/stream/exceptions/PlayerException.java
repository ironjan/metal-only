package com.codingspezis.android.metalonly.player.stream.exceptions;

public class PlayerException extends Exception {
    public PlayerException(String detailMessage) {
        super(detailMessage);
    }

    public PlayerException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}

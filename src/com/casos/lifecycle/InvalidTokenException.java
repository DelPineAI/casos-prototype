package com.casos.lifecycle;

/** Thrown when a one-click token is unknown, expired, redeemed or for the wrong action. */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}

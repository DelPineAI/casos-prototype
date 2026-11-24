package com.casos.identity;

/** Thrown when a session lacks the role required for an action. */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}

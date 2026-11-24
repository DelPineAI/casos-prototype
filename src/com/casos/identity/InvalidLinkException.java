package com.casos.identity;

/** Thrown when a magic link is missing, expired or already used. */
public class InvalidLinkException extends RuntimeException {

    public InvalidLinkException(String message) {
        super(message);
    }
}

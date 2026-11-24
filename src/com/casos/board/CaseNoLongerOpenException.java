package com.casos.board;

/** Thrown when interest is expressed in a case that has been filled or has expired (S4.4). */
public class CaseNoLongerOpenException extends RuntimeException {

    public CaseNoLongerOpenException(String message) {
        super(message);
    }
}

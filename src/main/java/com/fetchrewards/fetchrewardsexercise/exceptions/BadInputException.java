package com.fetchrewards.fetchrewardsexercise.exceptions;

public class BadInputException extends RuntimeException {
    public BadInputException(String field) {
        super("JSON field " + field + " is zero, null or of a bad format.");
    }
}

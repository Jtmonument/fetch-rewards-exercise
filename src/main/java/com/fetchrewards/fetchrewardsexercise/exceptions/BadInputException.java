package com.fetchrewards.fetchrewardsexercise.exceptions;

public class BadInputException extends RuntimeException {
    public <T> BadInputException(String field, T value) {
        super("JSON field " + field + " has value '" + value
                + "', which is either zero, null, blank, or of a bad format.");
    }
}

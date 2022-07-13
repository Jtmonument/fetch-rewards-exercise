package com.fetchrewards.fetchrewardsexercise.exceptions;

public class BadInputException extends RuntimeException {
    public <T> BadInputException(String field, T value) {
        super("JSON field " + field + " has value '" + value
                + "', which is either null, blank, negative, or of a bad format.");
    }
}

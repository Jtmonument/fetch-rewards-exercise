package com.fetchrewards.fetchrewardsexercise.exceptions;

public class BadTimestampFormatException extends RuntimeException {
    public BadTimestampFormatException(String timestamp) {
        super("Timestamp '" + timestamp + "' should be in ISO-8601 format yyyy-MM-dd'T'HH:mm:ss'Z'");
    }
}

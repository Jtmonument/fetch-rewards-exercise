package com.fetchrewards.fetchrewardsexercise.exceptions;

public class JsonParseException extends RuntimeException {
    public JsonParseException(String json) {
        super("Could not parse JSON '" + json + "'");
    }
}

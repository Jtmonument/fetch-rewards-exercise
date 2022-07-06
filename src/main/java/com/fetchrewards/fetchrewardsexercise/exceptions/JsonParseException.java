package com.fetchrewards.fetchrewardsexercise.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonParseException extends JsonProcessingException {
    public JsonParseException(String json) {
        super("Could not parse JSON '" + json + "'");
    }
}

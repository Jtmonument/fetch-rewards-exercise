package com.fetchrewards.fetchrewardsexercise.controllers;

import com.fetchrewards.fetchrewardsexercise.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FetchRewardsControllerAdvice {

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String accountNotFound(AccountNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler({BadInputException.class, BadTimestampFormatException.class, JsonParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badInput(RuntimeException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(InsufficientPointsException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String insufficientPoints(InsufficientPointsException exception) {
        return exception.getMessage();
    }
}

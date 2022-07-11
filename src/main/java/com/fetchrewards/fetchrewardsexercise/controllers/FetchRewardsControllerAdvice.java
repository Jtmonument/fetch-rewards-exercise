package com.fetchrewards.fetchrewardsexercise.controllers;

import com.fetchrewards.fetchrewardsexercise.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class FetchRewardsControllerAdvice {

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public JsonErrorResponse accountNotFound(AccountNotFoundException exception) {
        return new JsonErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler({BadInputException.class, BadTimestampFormatException.class, JsonParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonErrorResponse badInput(RuntimeException exception) {
        return new JsonErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(InsufficientPointsException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public JsonErrorResponse insufficientPoints(InsufficientPointsException exception) {
        return new JsonErrorResponse(LocalDateTime.now(), HttpStatus.NOT_ACCEPTABLE.value(), exception.getMessage());
    }
}

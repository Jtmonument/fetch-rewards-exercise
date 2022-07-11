package com.fetchrewards.fetchrewardsexercise.exceptions;

public class InsufficientPointsException extends RuntimeException {
    public InsufficientPointsException(Integer points, Integer attemptedToSpend) {
        super("You attempted to spend " + attemptedToSpend + " points, but you have " + points + " points.");
    }
}

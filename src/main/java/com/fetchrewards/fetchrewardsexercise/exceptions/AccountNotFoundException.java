package com.fetchrewards.fetchrewardsexercise.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("Account " + id + " not found");
    }
}

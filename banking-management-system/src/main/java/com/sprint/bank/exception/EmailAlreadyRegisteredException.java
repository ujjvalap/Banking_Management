package com.sprint.bank.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException() {
        super("An account with that email address already exists.");
    }
}

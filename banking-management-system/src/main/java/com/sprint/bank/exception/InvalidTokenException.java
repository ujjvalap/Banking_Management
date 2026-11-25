package com.sprint.bank.exception;


public class InvalidTokenException
        extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid or expired token");
    }
}

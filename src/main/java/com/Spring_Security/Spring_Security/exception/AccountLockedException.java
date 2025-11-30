package com.Spring_Security.Spring_Security.exception;



public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}

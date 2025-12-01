package com.Spring_Security.Spring_Security.exception;



public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}

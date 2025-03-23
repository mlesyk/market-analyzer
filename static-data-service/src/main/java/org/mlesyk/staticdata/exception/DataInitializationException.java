package org.mlesyk.staticdata.exception;

public class DataInitializationException extends RuntimeException {
    public DataInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
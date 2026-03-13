package com.egjarabo.circuitbreaker.exception;

public class ExternalApiException extends RuntimeException{

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

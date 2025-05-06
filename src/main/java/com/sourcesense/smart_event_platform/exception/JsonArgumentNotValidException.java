package com.sourcesense.smart_event_platform.exception;

public class JsonArgumentNotValidException extends RuntimeException {
    public JsonArgumentNotValidException(String message) {
        super(message);
    }

    public JsonArgumentNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}

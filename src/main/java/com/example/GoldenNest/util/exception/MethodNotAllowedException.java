package com.example.GoldenNest.util.exception;

//METHOD_NOT_ALLOWED (405)
public class MethodNotAllowedException extends RuntimeException {
    public MethodNotAllowedException(String message) {
        super(message);
    }
}

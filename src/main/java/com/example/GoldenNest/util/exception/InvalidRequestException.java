package com.example.GoldenNest.util.exception;

//BAD_REQUEST (400)
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}

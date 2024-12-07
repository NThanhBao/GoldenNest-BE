package com.example.GoldenNest.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

//FORBIDDEN (403)
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4893320765855582206L;

    public UnauthorizedException(String message) {
        super(message);
    }
}

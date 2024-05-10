package com.turnosmedicos.turnosmedicos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    private static final Long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }
}

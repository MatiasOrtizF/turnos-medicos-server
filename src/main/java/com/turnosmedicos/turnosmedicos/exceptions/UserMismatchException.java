package com.turnosmedicos.turnosmedicos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UserMismatchException extends RuntimeException {
    private static final Long serialVersionUID = 1L;

    public UserMismatchException(String message) {
        super(message);
    }
}

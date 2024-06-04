package com.turnosmedicos.turnosmedicos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AppointmentAlreadyExistingException extends RuntimeException {
    private static final Long serialVersionUID = 1L;

    public AppointmentAlreadyExistingException(String message) {
        super(message);
    }
}

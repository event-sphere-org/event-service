package com.eventsphere.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new instance of {@code UserAlreadyExistsException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public AlreadyExistsException(String message) {
        super(message);
    }
}
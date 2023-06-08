package com.eventsphere.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EventNotValidException extends RuntimeException {

    /**
     * Constructs a new instance of {@code EventNotValidException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public EventNotValidException(String message) {
        super(message);
    }
}
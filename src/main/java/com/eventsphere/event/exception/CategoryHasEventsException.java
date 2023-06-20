package com.eventsphere.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CategoryHasEventsException extends RuntimeException {
    public CategoryHasEventsException(String message) {
        super(message);
    }
}

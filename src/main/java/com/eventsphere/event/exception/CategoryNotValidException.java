package com.eventsphere.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CategoryNotValidException extends RuntimeException {
    public CategoryNotValidException(String message) {
        super(message);
    }
}

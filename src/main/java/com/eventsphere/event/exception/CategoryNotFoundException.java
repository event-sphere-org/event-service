package com.eventsphere.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super("Can't find the category with the id " + id);
    }

    public CategoryNotFoundException(String name) {
        super("Can't find the category with the name " + name);
    }
}

package com.eventsphere.event.exception;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(Long id) {
        super("Can't find the category with the id " + id);
    }
}

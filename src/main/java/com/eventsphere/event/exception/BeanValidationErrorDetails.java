package com.eventsphere.event.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents the details of bean validation errors.
 */
@Getter
public class BeanValidationErrorDetails {

    private final LocalDateTime timestamp;
    private final Map<String, List<String>> message;
    private final String details;

    /**
     * Constructs a new instance of {@code BeanValidationErrorDetails}.
     *
     * @param timestamp The timestamp of the error.
     * @param message   The map of field names and their corresponding error messages.
     * @param details   Additional details about the error.
     */
    public BeanValidationErrorDetails(LocalDateTime timestamp, Map<String, List<String>> message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}

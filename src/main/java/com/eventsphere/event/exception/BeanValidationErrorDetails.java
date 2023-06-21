package com.eventsphere.event.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents the details of bean validation errors.
 */
@Getter
@AllArgsConstructor
public class BeanValidationErrorDetails {

    private final LocalDateTime timestamp;
    private final Map<String, List<String>> message;
    private final String details;
}

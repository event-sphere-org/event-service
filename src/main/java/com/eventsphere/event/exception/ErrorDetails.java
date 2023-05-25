package com.eventsphere.event.exception;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Represents the details of an error.
 */
@Getter
public class ErrorDetails {

    private final LocalDateTime timestamp;
    private final String message;
    private final String details;

    /**
     * Constructs a new instance of {@code ErrorDetails}.
     *
     * @param timestamp The timestamp of the error.
     * @param message   The error message.
     * @param details   Additional details about the error.
     */
    public ErrorDetails(LocalDateTime timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}
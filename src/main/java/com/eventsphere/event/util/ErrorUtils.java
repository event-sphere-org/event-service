package com.eventsphere.event.util;

import lombok.experimental.UtilityClass;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ErrorUtils {

    /**
     * Convert a list of field errors to a map of field names and their corresponding error messages.
     *
     * @param fieldErrors the list of field errors
     * @return a map containing field names as keys and error messages as values
     */
    public Map<String, List<String>> getFieldErrors(List<FieldError> fieldErrors) {
        Map<String, List<String>> errorsMap = new HashMap<>();

        for (FieldError error : fieldErrors) {
            errorsMap.putIfAbsent(error.getField(), new ArrayList<>());
            errorsMap.get(error.getField()).add(error.getDefaultMessage());
        }

        return errorsMap;
    }
}

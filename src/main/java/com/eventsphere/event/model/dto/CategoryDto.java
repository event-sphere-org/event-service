package com.eventsphere.event.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {

    @Size(min = 3, message = "Name must be at least 3 characters")
    @Size(max = 50, message = "Name must be no more than 50 characters")
    private String name;
}

package com.eventsphere.event.model.dto;

import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class CategoryWithEventsDto extends RepresentationModel<CategoryWithEventsDto> {
    private Long id;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Set<Event> events;

    public CategoryWithEventsDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
        this.events = category.getEvents();
    }
}

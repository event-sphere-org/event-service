package com.eventsphere.event.model.dto.adapter;

import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventUpdateDto;
import com.eventsphere.event.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventUpdateDtoAdapter {

    private final CategoryService categoryService;

    public Event updateEventFromDto(Event event, EventUpdateDto dto) {
        Category category = categoryService.get(dto.getCategory());

        return Event.builder()
                .id(event.getId())
                .creatorId(event.getCreatorId())
                .title(dto.getTitle() != null ? dto.getTitle() : event.getTitle())
                .description(dto.getDescription() != null ? dto.getDescription() : event.getDescription())
                .imageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : event.getImageUrl())
                .location(dto.getLocation() != null ? dto.getLocation() : event.getLocation())
                .date(dto.getDate() != null ? dto.getDate() : event.getDate())
                .time(dto.getTime() != null ? dto.getTime() : event.getTime())
                .category(category)
                .build();
    }
}

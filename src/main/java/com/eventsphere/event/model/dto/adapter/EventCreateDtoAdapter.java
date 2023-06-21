package com.eventsphere.event.model.dto.adapter;

import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventCreateDto;
import com.eventsphere.event.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventCreateDtoAdapter {

    private final CategoryService categoryService;

    public Event fromDto(EventCreateDto dto) {
        Category category = categoryService.get(dto.getCategory());

        return Event.builder()
                .creatorId(dto.getCreatorId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .location(dto.getLocation())
                .date(dto.getDate())
                .time(dto.getTime())
                .category(category)
                .build();
    }
}

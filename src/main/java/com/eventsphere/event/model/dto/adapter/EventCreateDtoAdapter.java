package com.eventsphere.event.model.dto.adapter;

import com.eventsphere.event.exception.CategoryNotFoundException;
import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventCreateDto;
import com.eventsphere.event.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventCreateDtoAdapter {

    private final CategoryRepository categoryRepository;

    public Event fromDto(EventCreateDto dto) {
        Category category = categoryRepository.findByName(dto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategory()));

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

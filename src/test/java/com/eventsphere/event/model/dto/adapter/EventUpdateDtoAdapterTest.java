package com.eventsphere.event.model.dto.adapter;

import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventUpdateDto;
import com.eventsphere.event.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EventUpdateDtoAdapterTest {

    private CategoryService categoryService;
    private EventUpdateDtoAdapter adapter;

    @BeforeEach
    void setUp() {
        categoryService = mock(CategoryService.class);
        adapter = new EventUpdateDtoAdapter(categoryService);
    }

    @Test
    void updateEventFromDto_ValidDto_ReturnsUpdatedEvent() {
        // Given
        Event event = new Event();
        event.setId(1L);
        event.setCreatorId(1L);
        event.setTitle("Old Title");
        event.setDescription("Old Description");
        event.setImageUrl("https://example.com/old-image.jpg");
        event.setLocation("Old Location");
        event.setDate(Date.valueOf("2023-06-13"));
        event.setTime(Time.valueOf("14:00:00"));
        event.setCategory(new Category());

        EventUpdateDto dto = new EventUpdateDto();
        dto.setTitle("New Title");
        dto.setDescription("New Description");
        dto.setImageUrl("https://example.com/new-image.jpg");
        dto.setLocation("New Location");
        dto.setDate(Date.valueOf("2023-06-13"));
        dto.setTime(Time.valueOf("15:00:00"));
        dto.setCategory("Test Category");

        Category category = new Category();
        category.setId(2L);
        category.setName("Test Category");

        when(categoryService.get("Test Category")).thenReturn(category);

        // When
        Event updatedEvent = adapter.updateEventFromDto(event, dto);

        // Then
        assertEquals(event.getId(), updatedEvent.getId());
        assertEquals(event.getCreatorId(), updatedEvent.getCreatorId());
        assertEquals(dto.getTitle(), updatedEvent.getTitle());
        assertEquals(dto.getDescription(), updatedEvent.getDescription());
        assertEquals(dto.getImageUrl(), updatedEvent.getImageUrl());
        assertEquals(dto.getLocation(), updatedEvent.getLocation());
        assertEquals(dto.getDate(), updatedEvent.getDate());
        assertEquals(dto.getTime(), updatedEvent.getTime());
        assertEquals(category, updatedEvent.getCategory());

        verify(categoryService).get("Test Category");
        verifyNoMoreInteractions(categoryService);
    }
}

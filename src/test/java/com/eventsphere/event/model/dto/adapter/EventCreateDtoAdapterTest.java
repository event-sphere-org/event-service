package com.eventsphere.event.model.dto.adapter;

import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventCreateDto;
import com.eventsphere.event.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EventCreateDtoAdapterTest {

    private CategoryService categoryService;
    private EventCreateDtoAdapter adapter;

    @BeforeEach
    void setUp() {
        categoryService = mock(CategoryService.class);
        adapter = new EventCreateDtoAdapter(categoryService);
    }

    @Test
    void fromEventCreateDtoToEvent() {
        // Given
        EventCreateDto dto = new EventCreateDto();
        dto.setCreatorId(1L);
        dto.setTitle("Test Event");
        dto.setDescription("Test Description");
        dto.setImageUrl("https://example.com/image.jpg");
        dto.setLocation("Test Location");
        dto.setDate(Date.valueOf("2023-06-13"));
        dto.setTime(Time.valueOf("14:00:00"));
        dto.setCategory("Test Category");

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        when(categoryService.get("Test Category")).thenReturn(category);

        // When
        Event event = adapter.fromDto(dto);

        // Then
        assertEquals(dto.getCreatorId(), event.getCreatorId());
        assertEquals(dto.getTitle(), event.getTitle());
        assertEquals(dto.getDescription(), event.getDescription());
        assertEquals(dto.getImageUrl(), event.getImageUrl());
        assertEquals(dto.getLocation(), event.getLocation());
        assertEquals(dto.getDate(), event.getDate());
        assertEquals(dto.getTime(), event.getTime());
        assertEquals(category, event.getCategory());

        verify(categoryService).get("Test Category");
        verifyNoMoreInteractions(categoryService);
    }
}

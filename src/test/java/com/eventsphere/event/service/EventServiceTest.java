package com.eventsphere.event.service;

import com.eventsphere.event.exception.EventNotFoundException;
import com.eventsphere.event.exception.EventNotValidException;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventCreateDto;
import com.eventsphere.event.model.dto.EventUpdateDto;
import com.eventsphere.event.model.dto.adapter.EventCreateDtoAdapter;
import com.eventsphere.event.model.dto.adapter.EventUpdateDtoAdapter;
import com.eventsphere.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventCreateDtoAdapter eventCreateDtoAdapter;

    @Mock
    private EventUpdateDtoAdapter eventUpdateDtoAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventService(eventRepository, eventCreateDtoAdapter, eventUpdateDtoAdapter);
    }

    @Test
    void getAllEvents() {
        // Given
        List<Event> expectedEvents = new ArrayList<>();
        when(eventRepository.findAll()).thenReturn(expectedEvents);

        // When
        List<Event> actualEvents = eventService.getAll(0, 10);

        // Then
        assertSame(expectedEvents, actualEvents);
        verify(eventRepository).findAll();
    }

    @Test
    void getExistingEvent() {
        // Given
        Long eventId = 1L;
        Event expectedEvent = new Event();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(expectedEvent));

        // When
        Event actualEvent = eventService.get(eventId);

        // Then
        assertSame(expectedEvent, actualEvent);
        verify(eventRepository).findById(eventId);
    }

    @Test
    void getNonExistingEvent() {
        // Given
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(EventNotFoundException.class, () -> eventService.get(eventId));
        verify(eventRepository).findById(eventId);
    }

    @Test
    void saveValidEvent() {
        // Given
        Event event = new Event();
        Event expectedEvent = new Event();
        when(eventRepository.save(event)).thenReturn(expectedEvent);

        // When
        Event savedEvent = eventService.save(event);

        // Then
        assertSame(expectedEvent, savedEvent);
        assertNull(event.getCreatedAt());
        assertNull(event.getUpdatedAt());
        verify(eventRepository).save(event);
    }

    @Test
    void saveInvalidEvent() {
        // Given
        Event event = new Event();
        when(eventRepository.save(event)).thenThrow(new RuntimeException("Invalid Event data"));

        // When and Then
        assertThrows(EventNotValidException.class, () -> eventService.save(event));
        verify(eventRepository).save(event);
    }

    @Test
    void createEvent() {
        // Given
        EventCreateDto eventCreateDto = new EventCreateDto();
        Event createdEvent = new Event();
        when(eventCreateDtoAdapter.fromDto(eventCreateDto)).thenReturn(createdEvent);
        when(eventRepository.save(createdEvent)).thenReturn(createdEvent);

        // When
        Event savedEvent = eventService.create(eventCreateDto);

        // Then
        assertSame(createdEvent, savedEvent);
        verify(eventCreateDtoAdapter).fromDto(eventCreateDto);
        verify(eventRepository).save(createdEvent);
    }

    @Test
    void updateExistingEvent() {
        // Given
        Long eventId = 1L;
        EventUpdateDto eventUpdateDto = new EventUpdateDto();
        Event existingEvent = new Event();
        Event updatedEvent = new Event();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventUpdateDtoAdapter.updateEventFromDto(existingEvent, eventUpdateDto)).thenReturn(updatedEvent);
        when(eventRepository.save(updatedEvent)).thenReturn(updatedEvent);

        // When
        Event result = eventService.update(eventId, eventUpdateDto);

        // Then
        assertSame(updatedEvent, result);
        verify(eventUpdateDtoAdapter).updateEventFromDto(existingEvent, eventUpdateDto);
        verify(eventRepository).save(updatedEvent);
    }

    @Test
    void updateNonExistingEvent() {
        // Given
        Long eventId = 1L;
        EventUpdateDto eventUpdateDto = new EventUpdateDto();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(EventNotFoundException.class, () -> eventService.update(eventId, eventUpdateDto));
        verifyNoInteractions(eventUpdateDtoAdapter);
    }

    @Test
    void deleteExistingEvent() {
        // Given
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(true);

        // When
        eventService.delete(eventId);

        // Then
        verify(eventRepository).existsById(eventId);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void deleteNonExistingEvent() {
        // Given
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        // When and Then
        assertThrows(EventNotFoundException.class, () -> eventService.delete(eventId));
        verify(eventRepository).existsById(eventId);
    }
}
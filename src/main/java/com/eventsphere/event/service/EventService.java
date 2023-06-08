package com.eventsphere.event.service;

import com.eventsphere.event.exception.AlreadyExistsException;
import com.eventsphere.event.exception.EventNotFoundException;
import com.eventsphere.event.exception.EventNotValidException;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventCreateDto;
import com.eventsphere.event.model.dto.EventUpdateDto;
import com.eventsphere.event.model.dto.adapter.EventCreateDtoAdapter;
import com.eventsphere.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final EventCreateDtoAdapter eventCreateDtoAdapter;

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public Event get(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    public Event save(Event event) {
        Event savedEvent;

        try {
            event.setCreatedAt(null);
            event.setUpdatedAt(null);

            savedEvent = eventRepository.save(event);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new EventNotValidException("Invalid Event data: " + ex.getMessage());
        }

        return savedEvent;
    }

    public Event create(EventCreateDto eventCreateDto) {
        Event createdEvent = eventCreateDtoAdapter.fromDto(eventCreateDto);

        // TODO Check if user exists

        return eventRepository.save(createdEvent);
    }

    public Event update(Long eventId, EventUpdateDto eventUpdateDto) {
        Event eventFromDb = get(eventId);

        if (eventUpdateDto.getTitle() != null &&
                checkTitleUpdate(eventFromDb.getTitle(), eventUpdateDto.getTitle())) {
            eventFromDb.setTitle(eventUpdateDto.getTitle());
        }

        if (eventUpdateDto.getDescription() != null) {
            eventFromDb.setDescription(eventUpdateDto.getDescription());
        }

        if (eventUpdateDto.getImageUrl() != null) {
            eventFromDb.setImageUrl(eventUpdateDto.getImageUrl());
        }

        if (eventUpdateDto.getLocation() != null) {
            eventFromDb.setLocation(eventUpdateDto.getLocation());
        }

        if (eventUpdateDto.getTime() != null) {
            eventFromDb.setTime(eventUpdateDto.getTime());
        }

        return save(eventFromDb);
    }

    public boolean checkTitleUpdate(String titleFromDb, String updatedTitle) {
        if (!updatedTitle.equals(titleFromDb) && eventRepository.existsByTitle(updatedTitle)) {
            throw new AlreadyExistsException("This title is already registered");
        }
        return true;
    }

    public void delete(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new EventNotFoundException(id);
        }
    }

}

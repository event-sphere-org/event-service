package com.eventsphere.event.service;

import com.eventsphere.event.exception.AlreadyExistsException;
import com.eventsphere.event.exception.EventNotFoundException;
import com.eventsphere.event.exception.EventNotValidException;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventDto;
import com.eventsphere.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;


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

    public Event create(Event user) {
        Event createdUser;

        if (eventRepository.existsByTitle(user.getTitle())) {
            throw new AlreadyExistsException("This username is already registered");
        } else {
            createdUser = save(user);
        }

        return createdUser;
    }

    public Event update(Long eventId, EventDto eventDto) {
        Event eventFromDb = get(eventId);

        if (eventDto.getTitle() != null &&
                checkTitleUpdate(eventFromDb.getTitle(), eventDto.getTitle())) {
            eventFromDb.setTitle(eventDto.getTitle());
        }

        if (eventDto.getDescription() != null) {
            eventFromDb.setDescription(eventDto.getDescription());
        }

        if (eventDto.getImageUrl() != null) {
            eventFromDb.setImageUrl(eventDto.getImageUrl());
        }

        if (eventDto.getLocation() != null) {
            eventFromDb.setLocation(eventDto.getLocation());
        }

        if (eventDto.getTime() != null) {
            eventFromDb.setTime(eventDto.getTime());
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

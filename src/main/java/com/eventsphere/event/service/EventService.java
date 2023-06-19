package com.eventsphere.event.service;

import com.eventsphere.event.exception.EventNotFoundException;
import com.eventsphere.event.exception.EventNotValidException;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventCreateDto;
import com.eventsphere.event.model.dto.EventUpdateDto;
import com.eventsphere.event.model.dto.adapter.EventCreateDtoAdapter;
import com.eventsphere.event.model.dto.adapter.EventUpdateDtoAdapter;
import com.eventsphere.event.repository.EventRepository;
import com.eventsphere.event.util.RabbitMqSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;

    private final EventCreateDtoAdapter eventCreateDtoAdapter;

    private final EventUpdateDtoAdapter eventUpdateDtoAdapter;

    private final RabbitMqSender sender;

    public List<Event> getAll(final int page, final int size) {
        Pageable pageable = PageRequest.of(page, size);

        return eventRepository.findAll(pageable).getContent();
    }

    public Event get(final Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    public List<Event> getAllByCreatorId(final Long id, final int page, final int size) {
        Pageable pageable = PageRequest.of(page, size);

        return eventRepository.findByCreatorId(id, pageable).getContent();
    }

    public Event save(final Event event) {
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

    public Event create(final EventCreateDto eventCreateDto) {
        Event createdEvent = eventCreateDtoAdapter.fromDto(eventCreateDto);

        // TODO Check if user exists

        return eventRepository.save(createdEvent);
    }

    public Event update(final Long eventId, final EventUpdateDto eventUpdateDto) {
        Event updatedEvent = eventUpdateDtoAdapter.updateEventFromDto(get(eventId), eventUpdateDto);

        return save(updatedEvent);
    }

    public void delete(final Long id) {
        if (eventRepository.existsById(id)) {
            log.info("Sending deleted event id {} message to user-service", id);
            sender.sendDeletedEventId(id);
            eventRepository.deleteById(id);
        } else {
            throw new EventNotFoundException(id);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.user.delete}")
    @Transactional
    public void deleteAllByCreatorId(final Long id) {
        log.info("Received delete user message from user-service with id: " + id);
        eventRepository.deleteAllByCreatorId(id);
        log.info("Deleted all events for user with id: " + id);
    }
}

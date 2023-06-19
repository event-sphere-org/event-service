package com.eventsphere.event.controller;

import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventCreateDto;
import com.eventsphere.event.model.dto.EventUpdateDto;
import com.eventsphere.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("v1/events")
@RequiredArgsConstructor
public class EventController {

    private static final String GET_EVENT_REL = "get-event";
    private static final String CREATE_EVENT_REL = "create-event";
    private static final String GET_ALL_EVENTS_REL = "get-all-events";
    private static final String SELF_REL = "self";

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<CollectionModel<Event>> getAllEvents(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        List<Event> events = eventService.getAll(page, size);

        for (Event event : events) {
            event.add(
                    linkTo(methodOn(EventController.class).getEvent(event.getId())).withRel(GET_EVENT_REL)
            );
        }

        CollectionModel<Event> eventCollectionModel = CollectionModel.of(events);
        eventCollectionModel.add(
                linkTo(methodOn(EventController.class).getAllEvents(0, 10)).withRel(SELF_REL),
                linkTo(methodOn(EventController.class).createEvent(new EventCreateDto())).withRel(CREATE_EVENT_REL)
        );

        return ResponseEntity.ok(eventCollectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable final Long id) {
        Event event = eventService.get(id);

        event.add(
                linkTo(methodOn(EventController.class).getEvent(id)).withRel(SELF_REL),
                linkTo(methodOn(EventController.class).getAllEvents(0, 10)).withRel(GET_ALL_EVENTS_REL),
                linkTo(methodOn(EventController.class).createEvent(new EventCreateDto())).withRel(CREATE_EVENT_REL)
        );

        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody final EventCreateDto event) {
        Event createdEvent = eventService.create(event);

        createdEvent.add(
                linkTo(methodOn(EventController.class).createEvent(event)).withRel(SELF_REL),
                linkTo(methodOn(EventController.class).getAllEvents(0, 10)).withRel(GET_ALL_EVENTS_REL),
                linkTo(methodOn(EventController.class).getEvent(createdEvent.getId())).withRel(GET_EVENT_REL)
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEvent.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdEvent);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable final Long id,
            @Valid @RequestBody final EventUpdateDto eventUpdateDto
    ) {
        Event updatedEvent = eventService.update(id, eventUpdateDto);

        updatedEvent.add(
                linkTo(methodOn(EventController.class).updateEvent(id, eventUpdateDto)).withRel(SELF_REL),
                linkTo(methodOn(EventController.class).getAllEvents(0, 10)).withRel(GET_ALL_EVENTS_REL),
                linkTo(methodOn(EventController.class).getEvent(updatedEvent.getId())).withRel(GET_EVENT_REL)
        );

        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable final Long id) {
        eventService.delete(id);
        return ResponseEntity.ok().build();
    }
}



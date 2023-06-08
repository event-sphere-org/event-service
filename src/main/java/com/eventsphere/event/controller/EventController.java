package com.eventsphere.event.controller;

import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventDto;
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
    public ResponseEntity<CollectionModel<Event>> getAllEvents() {
        List<Event> events = eventService.getAll();

        for (Event event : events) {
            event.add(
                    linkTo(methodOn(EventController.class).getEvent(event.getId())).withRel(GET_EVENT_REL)
            );
        }

        CollectionModel<Event> eventCollectionModel = CollectionModel.of(events);
        eventCollectionModel.add(
                linkTo(methodOn(EventController.class).getAllEvents()).withRel(SELF_REL),
                linkTo(methodOn(EventController.class).createEvent(new Event())).withRel(CREATE_EVENT_REL)
        );

        return ResponseEntity.ok(eventCollectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        Event event = eventService.get(id);

        event.add(
                linkTo(methodOn(EventController.class).getEvent(id)).withRel(SELF_REL),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel(GET_ALL_EVENTS_REL),
                linkTo(methodOn(EventController.class).createEvent(event)).withRel(CREATE_EVENT_REL)
        );

        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        Event createdUser = eventService.create(event);

        createdUser.add(
                linkTo(methodOn(EventController.class).createEvent(event)).withRel(SELF_REL),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel(GET_ALL_EVENTS_REL),
                linkTo(methodOn(EventController.class).getEvent(createdUser.getId())).withRel(GET_EVENT_REL)
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody EventDto userDto) {
        Event updatedUser = eventService.update(id, userDto);

        updatedUser.add(
                linkTo(methodOn(EventController.class).updateEvent(id, userDto)).withRel(SELF_REL),
                linkTo(methodOn(EventController.class).getAllEvents()).withRel(GET_ALL_EVENTS_REL),
                linkTo(methodOn(EventController.class).createEvent(updatedUser)).withRel(CREATE_EVENT_REL)
        );

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.ok().build();
    }
}



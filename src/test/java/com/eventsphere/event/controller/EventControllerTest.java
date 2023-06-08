package com.eventsphere.event.controller;

import com.eventsphere.event.exception.AlreadyExistsException;
import com.eventsphere.event.exception.EventNotFoundException;
import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventUpdateDto;
import com.eventsphere.event.service.EventService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void getAllEventsTest() throws Exception {

        List<Event> events = new ArrayList<>();
        events.add(new Event(1L, 1L, "Event Title", "Event description", "Event image-url", "City Concert Hall", "2023-06-15", "19:00:00", "Art"));
        when(eventService.getAll()).thenReturn(events);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.eventList").isArray())
                .andExpect(jsonPath("$._embedded.eventList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.eventList[0].creatorId").value(1L))
                .andExpect(jsonPath("$._embedded.eventList[0].title").value("Event Title"))
                .andExpect(jsonPath("$._embedded.eventList[0].description").value("Event description"))
                .andExpect(jsonPath("$._embedded.eventList[0].imageUrl").value("Event image-url"))
                .andExpect(jsonPath("$._embedded.eventList[0].location").value("City Concert Hall"))
                .andExpect(jsonPath("$._embedded.eventList[0].date").value("2023-06-15"))
                .andExpect(jsonPath("$._embedded.eventList[0].time").value("19:00:00"));
        //.andExpect(jsonPath("$.eventList[0].category").value("Art"));
    }


    @Test
    void getExistingEventTest() throws Exception {

        Event event = new Event(1L, 1L, "Event Title", "Event description", "Event image-url", "City Concert Hall", "2023-06-15", "19:00:00", "Music");
        when(eventService.get(1L)).thenReturn(event);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.creatorId").value(1L))
                .andExpect(jsonPath("$.title").value("Event Title"))
                .andExpect(jsonPath("$.description").value("Event description"))
                .andExpect(jsonPath("$.imageUrl").value("Event image-url"))
                .andExpect(jsonPath("$.location").value("City Concert Hall"))
                .andExpect(jsonPath("$.date").value("2023-06-15"))
                .andExpect(jsonPath("$.time").value("19:00:00"))
                .andExpect(jsonPath("$.category.name").value("Music"));
    }

    @Test
    void getNonExistingEventTest() throws Exception {
        // Given
        when(eventService.get(2L)).thenThrow(EventNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/events/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createValidEventTest() throws Exception {
        // Given
        Event event = new Event(1L, 1L, "Event Title", "Event description", "Event image-url", "City Concert Hall", "2023-06-15", "19:00:00", "Music");
        when(eventService.create(event)).thenReturn(event);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(event)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.creatorId").value(1L))
                .andExpect(jsonPath("$.title").value("Event Title"))
                .andExpect(jsonPath("$.description").value("Event description"))
                .andExpect(jsonPath("$.imageUrl").value("Event image-url"))
                .andExpect(jsonPath("$.location").value("City Concert Hall"))
                .andExpect(jsonPath("$.date").value("2023-06-15"))
                .andExpect(jsonPath("$.time").value("19:00:00"))
                .andExpect(jsonPath("$.category.name").value("Music"));
    }

    @Test
    void createInvalidEventTest() throws Exception {

        Event event = new Event(3333L, 5555L, "p", "Event descr", "Event image-ur", "l", "2020-06-15", "28:00:00", "Music");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(event)))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"));
    }

    @Test
    void createEventAlreadyExistsByTitle() throws Exception {
        Event event = new Event(2L, 2L, "Event Title2", "Event description2", "Event image-url2", "City Concert Hall2", "2023-07-20", "11:00:00", "Category name2");
        when(eventService.create(event)).thenAnswer(invocation -> {
            throw new AlreadyExistsException("This title is already registered");
        });

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(event)))
                .andExpect(status().isConflict())
                .andExpect(header().doesNotExist("location"));
    }

    @Test
    void validUpdateEventTest() throws Exception {

        EventUpdateDto eventUpdateDto = new EventUpdateDto();
        eventUpdateDto.setTitle("updateTitle");
        eventUpdateDto.setDescription("updateDescription");
        eventUpdateDto.setImageUrl("Update imageUrl");
        eventUpdateDto.setLocation("updateLocation");
        eventUpdateDto.setDate(new Date(2023 - 11 - 2));
        eventUpdateDto.setTime(new Time(15 - 30));
        eventUpdateDto.setCategory(new Category(3L, "Music"));
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(eventUpdateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void invalidUpdateEventTest() throws Exception {

        EventUpdateDto eventUpdateDto = new EventUpdateDto();
        eventUpdateDto.setTitle("t");
        eventUpdateDto.setDescription("d");
        eventUpdateDto.setImageUrl("Update imageUrl");
        eventUpdateDto.setLocation("l");
        eventUpdateDto.setDate(new Date(2002 - 11 - 2));
        eventUpdateDto.setTime(new Time(30));
        eventUpdateDto.setCategory(new Category());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(eventUpdateDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deleteEventSuccessful() throws Exception {

        Long eventId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/events/{id}", eventId))
                .andExpect(status().isOk());
        verify(eventService).delete(eventId);
    }

    @Test
    void deleteNonExistingUser() throws Exception {

        Long eventId = 1L;

        doThrow(EventNotFoundException.class)
                .when(eventService)
                .delete(eventId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/events/{id}", eventId))
                .andExpect(status().isNotFound());
        verify(eventService).delete(eventId);
    }
}
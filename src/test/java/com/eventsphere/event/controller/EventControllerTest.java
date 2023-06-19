package com.eventsphere.event.controller;

import com.eventsphere.event.exception.EventNotFoundException;
import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.EventCreateDto;
import com.eventsphere.event.model.dto.EventUpdateDto;
import com.eventsphere.event.service.EventService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    private Gson gson;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .registerTypeAdapter(Time.class, (JsonSerializer<Time>) (time, type, context) -> {
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    String formattedTime = timeFormat.format(time);
                    return new JsonPrimitive(formattedTime);
                })
                .create();
    }

    @Test
    void getAllEventsTest() throws Exception {
        // Given
        List<Event> events = new ArrayList<>();
        events.add(
                Event.builder()
                        .id(1L)
                        .creatorId(1L)
                        .title("Event Title")
                        .description("Event description")
                        .imageUrl("Event image-url")
                        .location("Sample Location")
                        .category(new Category(1L, "Test Category"))
                        .build()
        );
        when(eventService.getAll(0, 10)).thenReturn(events);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.eventList").isArray())
                .andExpect(jsonPath("$._embedded.eventList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.eventList[0].creatorId").value(1L))
                .andExpect(jsonPath("$._embedded.eventList[0].title").value("Event Title"))
                .andExpect(jsonPath("$._embedded.eventList[0].description").value("Event description"))
                .andExpect(jsonPath("$._embedded.eventList[0].imageUrl").value("Event image-url"))
                .andExpect(jsonPath("$._embedded.eventList[0].location").value("Sample Location"))
                .andExpect(jsonPath("$._embedded.eventList[0].category.name").value("Test Category"));
    }


    @Test
    void getExistingEventTest() throws Exception {
        // Given
        Event event = Event.builder()
                .id(1L)
                .creatorId(1L)
                .title("Event Title")
                .description("Event description")
                .imageUrl("Event image-url")
                .location("Sample Location")
                .date(Date.valueOf("2023-06-15"))
                .time(Time.valueOf("19:00:00"))
                .category(new Category(1L, "Test Category"))
                .build();
        when(eventService.get(1L)).thenReturn(event);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.creatorId").value(1L))
                .andExpect(jsonPath("$.title").value("Event Title"))
                .andExpect(jsonPath("$.description").value("Event description"))
                .andExpect(jsonPath("$.imageUrl").value("Event image-url"))
                .andExpect(jsonPath("$.location").value("Sample Location"))
                .andExpect(jsonPath("$.date").value("2023-06-15"))
                .andExpect(jsonPath("$.time").value("19:00:00"))
                .andExpect(jsonPath("$.category.name").value("Test Category"));
    }

    @Test
    void createValidEventTest() throws Exception {
        // Given
        EventCreateDto eventCreateDto = new EventCreateDto(
                1L,
                "Event Title",
                "Event description",
                "Event image-url",
                "City Concert Hall",
                Date.valueOf("2023-06-15"),
                Time.valueOf("19:00:00"),
                "Music"
        );

        Event event = Event.builder()
                .id(1L)
                .creatorId(1L)
                .title("Event Title")
                .description("Event description")
                .imageUrl("Event image-url")
                .location("City Concert Hall")
                .date(Date.valueOf("2023-06-15"))
                .time(Time.valueOf("19:00:00"))
                .category(new Category(1L, "Music"))
                .build();

        when(eventService.create(Mockito.any(EventCreateDto.class))).thenReturn(event);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(eventCreateDto)))
                .andExpect(status().isCreated())
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
        // Given
        EventCreateDto eventCreateDto = new EventCreateDto(
                0L,
                "e",
                "Location",
                "",
                "",
                Date.valueOf("2023-06-15"),
                Time.valueOf("19:00:00"),
                "Music"
        );

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(eventCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"))
                .andExpect(jsonPath("$.message.location[0]").value("Location must be at least 3 characters"))
                .andExpect(jsonPath("$.message.title[0]").value("Title must be at least 3 characters"));
    }

    @Test
    void validUpdateEventTest() throws Exception {
        // Given
        EventUpdateDto eventUpdateDto = new EventUpdateDto();
        eventUpdateDto.setTitle("updateTitle");
        eventUpdateDto.setDescription("updateDescription");
        eventUpdateDto.setImageUrl("Update imageUrl");
        eventUpdateDto.setLocation("updateLocation");
        eventUpdateDto.setDate(Date.valueOf("2123-09-11"));
        eventUpdateDto.setTime(Time.valueOf("19:00:00"));
        eventUpdateDto.setCategory("Music");

        Event event = Event.builder()
                .title("updateTitle")
                .description("updateDescription")
                .imageUrl("Update imageUrl")
                .location("updateLocation")
                .date(Date.valueOf("2123-09-11"))
                .time(Time.valueOf("19:00:00"))
                .category(new Category(1L, "Music"))
                .build();

        when(eventService.update(Mockito.eq(1L), Mockito.any(EventUpdateDto.class))).thenReturn(event);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(eventUpdateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void invalidUpdateEventTest() throws Exception {
        // Given
        EventUpdateDto eventUpdateDto = new EventUpdateDto();
        eventUpdateDto.setTitle("t");
        eventUpdateDto.setDescription("d");
        eventUpdateDto.setImageUrl("Update imageUrl");
        eventUpdateDto.setLocation("l");
        eventUpdateDto.setDate(Date.valueOf("2002-11-02"));
        eventUpdateDto.setCategory("");

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(eventUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.location[0]").value("Location must be at least 3 characters"))
                .andExpect(jsonPath("$.message.date[0]").value("Date can't be in the past =)"))
                .andExpect(jsonPath("$.message.title[0]").value("Title must be at least 3 characters"));
    }

    @Test
    void deleteEventSuccessful() throws Exception {
        // Given
        Long eventId = 1L;

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/events/{id}", eventId))
                .andExpect(status().isOk());
        verify(eventService).delete(eventId);
    }

    @Test
    void deleteNonExistingUser() throws Exception {
        // Given
        Long eventId = 1L;

        doThrow(EventNotFoundException.class)
                .when(eventService)
                .delete(eventId);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/events/{id}", eventId))
                .andExpect(status().isNotFound());
        verify(eventService).delete(eventId);
    }
}

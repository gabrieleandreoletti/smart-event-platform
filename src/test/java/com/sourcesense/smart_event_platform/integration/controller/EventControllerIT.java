package com.sourcesense.smart_event_platform.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcesense.smart_event_platform.configuration.Role;
import com.sourcesense.smart_event_platform.integration.IntegrationTest;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateEventRequest;
import com.sourcesense.smart_event_platform.persistance.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EventRepository eventRepository;

    Customer admin;

    private static final String BASE_PATH = "/api/v1/events";

    @BeforeEach
    void setUp() {

        admin = Customer.builder()
                .username("admin")
                .role(Role.ADMIN)
                .build();


        eventRepository.deleteAll();

        Event event1 = new Event("id", "name1", "description1", 21, LocalDateTime.of(2026, 1, 1, 21, 0), false);

        event1.getWaitList().add("customerInListId");

        Event event2 = Event.builder()
                .name("name2")
                .description("description2")
                .maxCapacity(30)
                .dateTime(LocalDateTime.of(2026, 1, 1, 16, 0))
                .build();


        eventRepository.saveAll(List.of(event1, event2));
    }

    @Test
    void insert_201() throws Exception {
        InsertEventRequest insertEventRequest = InsertEventRequest.builder()
                .name("name")
                .description("description")
                .maxCapacity(20)
                .isPrivate(false)
                .dateTime(LocalDateTime.of(2026, 1, 1, 21, 0))
                .build();

        Customer customer = Customer.builder()
                .role(Role.ORGANIZER)
                .username("username")
                .build();

        mockMvc.perform(post(BASE_PATH)
                        .with(user(customer))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertEventRequest)))
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name", is("name")),
                        jsonPath("$.description", is("description")),
                        jsonPath("$.dateTime", is("2026-01-01T21:00:00")),
                        jsonPath("$.maxCapacity", is(20)),
                        jsonPath("isPrivate", is(false))
                );
    }

    @Test
    void delete_204() throws Exception {

        mockMvc.perform(delete(BASE_PATH + "/id")
                        .with(user(admin)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string("true"));
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "2")
                        .with(user(admin)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].id", is("id")),
                        jsonPath("$.content[0].name", is("name1")),
                        jsonPath("$.content[0].description", is("description1")),
                        jsonPath("$.content[0].maxCapacity", is(21)),
                        jsonPath("$.content[0].dateTime", is("2026-01-01T21:00:00")),
                        jsonPath("$.content[1].name", is("name2")),
                        jsonPath("$.content[1].description", is("description2")),
                        jsonPath("$.content[1].maxCapacity", is(30)),
                        jsonPath("$.content[1].dateTime", is("2026-01-01T16:00:00"))
                );
    }

    @Test
    void getWaitlist() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/waitlist/id")
                        .with(user(admin)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.[0]").value(("customerInListId"))
                );
    }

    @Test
    void getFilteredEvents() {
    }

    @Test
    void update() throws Exception {
        UpdateEventRequest eventRequest = UpdateEventRequest.builder()
                .name("newName")
                .description("newDesc")
                .maxCapacity(30)
                .dateTime(LocalDateTime.of(2026, 5, 5, 10, 0))
                .build();

        mockMvc.perform(put(BASE_PATH + "/id")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest))
                        .with(user(admin)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value("id"),
                        jsonPath("$.name").value("newName"),
                        jsonPath("$.description").value("newDesc"),
                        jsonPath("$.maxCapacity").value(30),
                        jsonPath("$.dateTime").value("2026-05-05T10:00:00")
                );
    }

    @Test
    void findByID() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/id")
                        .with(user(admin)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value("id"),
                        jsonPath("$.name").value("name1"),
                        jsonPath("$.description").value("description1"),
                        jsonPath("$.maxCapacity").value(21),
                        jsonPath("$.dateTime").value("2026-01-01T21:00.00")
                );
    }

    @Test
    void findByDate() throws Exception {
        mockMvc.perform(get(BASE_PATH + "date/01-01-2026"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.[0].id").value("id"),
                        jsonPath("$.[0].name").value("name1"),
                        jsonPath("$.[0].description").value("description1"),
                        jsonPath("$.[0].maxCapacity").value("21"),
                        jsonPath("$.[0].dateTime").value("2026-01-01T21:00:00"),
                        jsonPath("$.[1].name").value("name2"),
                        jsonPath("$.[1].description").value("description2"),
                        jsonPath("$.[1].maxCapacity").value(30),
                        jsonPath("$.[1].dateTime").value("2026-01-01T21:00:00")
                );
    }
}
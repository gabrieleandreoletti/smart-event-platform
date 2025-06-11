package com.sourcesense.smart_event_platform.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcesense.smart_event_platform.configuration.Role;
import com.sourcesense.smart_event_platform.configuration.TestConfiguration;
import com.sourcesense.smart_event_platform.controller.implementation.EventControllerImpl;
import com.sourcesense.smart_event_platform.exception.EventNotFoundException;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateEventRequest;
import com.sourcesense.smart_event_platform.service.definition.EventService;
import com.sourcesense.smart_event_platform.utility.JwtUtility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventControllerImpl.class)
@Import(TestConfiguration.class)
class EventControllerImplTest {

    private static final String BASE_PATH = "/api/v1/events";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtility jwtUtility;

    @MockitoBean
    private EventService service;


    @Test
    void insert_201() throws Exception {

        //Utente autenticato che crea l'evento
        Customer organizer = Customer.builder()
                .id("id")
                .username("organizer")
                .role(Role.ORGANIZER)
                .build();

        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(organizer, null);

        //Request body per creazione evento
        InsertEventRequest request = InsertEventRequest.builder()
                .name("name")
                .description("description")
                .maxCapacity(30)
                .dateTime(LocalDateTime.of(2026, 1, 1, 21, 0))
                .isPrivate(false)
                .build();

        //Risultato aspettato
        EventDto expectedResult = EventDto.builder()
                .maxCapacity(30)
                .actualCapacity(0)
                .id("id")
                .organizerUsername(organizer.getUsername())
                .dateTime(LocalDateTime.of(2026, 1, 1, 21, 0))
                .isPrivate(false)
                .name("name")
                .description("description")
                .build();

        when(service.insert(eq(request), any(UsernamePasswordAuthenticationToken.class))).thenReturn(expectedResult);


        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(user(organizer))
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.dateTime", is("2026-01-01T21:00:00")))
                .andExpect(jsonPath("$.maxCapacity", is(30)))
                .andExpect(jsonPath("$.isPrivate", is(false)));
    }

    @Test
    void insert_400() throws Exception {
        //simulo una richiesta con campi nulli
        InsertEventRequest request = InsertEventRequest.builder().build();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.cause", is("org.springframework.web.bind.MethodArgumentNotValidException")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Bad Request")));

    }

    @Test
    void delete_204() throws Exception {
        when(service.delete("1234")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/1234"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string("true"));
    }

    @Test
    void delete_400() throws Exception {
        when(service.delete(anyString())).thenThrow(EventNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/1234"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cause", is("com.sourcesense.smart_event_platform.exception.EventNotFoundException")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Bad Request")))
        ;

    }


    @Test
    void findAll() throws Exception {
        List<EventDto> expectedDtoList = List.of(
                new EventDto("id1", "name1", "desc1", 2, 20, LocalDateTime.of(2026, 1, 1, 20, 0), "organizer", false),
                new EventDto("id2", "name2", "desc2", 4, 25, LocalDateTime.of(2026, 2, 2, 21, 0), "organizer", true)
        );

        Pageable pageable = PageRequest.of(0, 2);
        Page<EventDto> page = new PageImpl<>(expectedDtoList, pageable, 2);

        when(service.findAll(0, 2)).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                //contrtolli sul primo elemento della lista
                .andExpect(jsonPath("$.content[0].id", is("id1")))
                .andExpect(jsonPath("$.content[0].name", is("name1")))
                .andExpect(jsonPath("$.content[0].description", is("desc1")))
                .andExpect(jsonPath("$.content[0].actualCapacity", is(2)))
                .andExpect(jsonPath("$.content[0].maxCapacity", is(20)))
                .andExpect(jsonPath("$.content[0].dateTime", is("2026-01-01T20:00:00")))
                .andExpect(jsonPath("$.content[0].organizerUsername", is("organizer")))
                .andExpect(jsonPath("$.content[0].isPrivate", is(false)))
                //controlli sul secondo elemento della lista
                .andExpect(jsonPath("$.content[1].id", is("id2")))
                .andExpect(jsonPath("$.content[1].name", is("name2")))
                .andExpect(jsonPath("$.content[1].description", is("desc2")))
                .andExpect(jsonPath("$.content[1].actualCapacity", is(4)))
                .andExpect(jsonPath("$.content[1].maxCapacity", is(25)))
                .andExpect(jsonPath("$.content[1].dateTime", is("2026-02-02T21:00:00")))
                .andExpect(jsonPath("$.content[1].organizerUsername", is("organizer")))
                .andExpect(jsonPath("$.content[1].isPrivate", is(true)))
                //controllo sulla paginazione
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.pageable.pageSize", is(2)))
                .andExpect(jsonPath("$.totalElements", is(2))
                );

    }


    @Test
    void getWaitlist() throws Exception {
        List<String> eventWaitlist = List.of(
                "customerId1", "customerId2"
        );

        when(service.getWaitlist("eventId")).thenReturn(eventWaitlist);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/waitlist/eventId"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]", is("customerId1")))
                .andExpect(jsonPath("$.[1]", is("customerId2")));

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
                .dateTime(LocalDateTime.of(2026, 1, 1, 20, 0))
                .build();

        EventDto updatedEvent = EventDto.builder()
                .name("newName")
                .description("newDesc")
                .id("id")
                .maxCapacity(30)
                .dateTime(LocalDateTime.of(2026, 1, 1, 20, 0))
                .isPrivate(false)
                .build();


        when(service.update("id", eventRequest)).thenReturn(updatedEvent);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/id")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is("id")),
                        jsonPath("$.name", is("newName")),
                        jsonPath("$.description", is("newDesc")),
                        jsonPath("$.maxCapacity", is(30)),
                        jsonPath("$.dateTime", is("2026-01-01T20:00:00"))
                );

    }

    @Test
    void findByID() throws Exception {
        EventDto expectedDto = EventDto.builder()
                .name("name")
                .description("desc")
                .id("id")
                .maxCapacity(30)
                .dateTime(LocalDateTime.of(2026, 1, 1, 20, 0))
                .isPrivate(false)
                .build();

        when(service.findById("id")).thenReturn(expectedDto);


        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/id"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is("id")),
                        jsonPath("$.name", is("name")),
                        jsonPath("$.description", is("desc")),
                        jsonPath("$.maxCapacity", is(30)),
                        jsonPath("$.dateTime", is("2026-01-01T20:00:00"))
                );
    }

    @Test
    void findByDate() throws Exception {
        List<EventDto> expectedDto = List.of(
                EventDto.builder()
                        .id("id1")
                        .name("name1")
                        .dateTime(LocalDateTime.of(2026, 1, 1, 12, 30))
                        .build(),
                EventDto.builder()
                        .id("id2")
                        .name("name2")
                        .dateTime(LocalDateTime.of(2026, 1, 1, 18, 30))
                        .build()
        );

        LocalDate searchDate = LocalDate.of(2026, 1, 1);

        when(service.findByDate(searchDate)).thenReturn(expectedDto);


        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/date/01-01-2026"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.[0].id", is("id1")),
                        jsonPath("$.[0].name", is("name1")),
                        jsonPath("$.[0].dateTime", is("2026-01-01T12:30:00")),
                        jsonPath("$.[1].id", is("id2")),
                        jsonPath("$.[1].name", is("name2")),
                        jsonPath("$.[1].dateTime", is("2026-01-01T18:30:00"))
                );
    }
}
package com.sourcesense.smart_event_platform.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcesense.smart_event_platform.configuration.Role;
import com.sourcesense.smart_event_platform.configuration.TestConfiguration;
import com.sourcesense.smart_event_platform.controller.implementation.ReservationControllerImpl;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.dto.ReservationDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertReservationRequest;
import com.sourcesense.smart_event_platform.service.definition.ReservationService;
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

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestConfiguration.class)
@WebMvcTest(ReservationControllerImpl.class)
class ReservationControllerImplTest {


    private static final String BASE_PATH = "/api/v1/reservations";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private JwtUtility jwtUtility;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void insert_201() throws Exception {
        InsertReservationRequest insertReservationRequest = new InsertReservationRequest("eventId");

        Customer customer = Customer.builder()
                .role(Role.USER)
                .id("id")
                .username("username")
                .build();

        ReservationDto reservationDto = new ReservationDto("id", "customerId", "eventId");

        when(reservationService.insert(eq(insertReservationRequest), any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(reservationDto);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(user(customer))
                        .content(mapper.writeValueAsString(insertReservationRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("id")))
                .andExpect(jsonPath("$.customerId", is("customerId")))
                .andExpect(jsonPath("$.eventId", is("eventId")));
    }

    @Test
    void delete_204() throws Exception {
        when(reservationService.delete("reservationId")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/reservationId"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string("true"));
    }

    @Test
    void findAll_200() throws Exception {
        List<ReservationDto> expectedDtoList = List.of(
                new ReservationDto("id1", "customerId1", "eventId1"),
                new ReservationDto("id2", "customerId2", "eventId2")
        );

        Pageable pageable = PageRequest.of(0, 2);
        Page<ReservationDto> page = new PageImpl<>(expectedDtoList, pageable, 2);

        when(reservationService.findAll(0, 2)).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                //contrtolli sul primo elemento della lista
                .andExpect(jsonPath("$.content[0].id", is("id1")))
                .andExpect(jsonPath("$.content[0].eventId", is("eventId1")))
                .andExpect(jsonPath("$.content[0].customerId", is("customerId1")))
                //controlli sul secondo elemento della lista
                .andExpect(jsonPath("$.content[1].id", is("id2")))
                .andExpect(jsonPath("$.content[1].eventId", is("eventId2")))
                .andExpect(jsonPath("$.content[1].customerId", is("customerId2")))
                //controllo sulla paginazione
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.pageable.pageSize", is(2)))
                .andExpect(jsonPath("$.totalElements", is(2))
                );

    }

    @Test
    void findById_200() throws Exception {
        ReservationDto expected = new ReservationDto("id", "customerId", "eventId");

        when(reservationService.findById("id")).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("id")))
                .andExpect(jsonPath("$.eventId", is("eventId")))
                .andExpect(jsonPath("$.customerId", is("customerId")));
    }

    @Test
    void findByCustomer_200() throws Exception {
        Customer customer = Customer.builder()
                .id("id")
                .username("username")
                .role(Role.USER)
                .build();

        ReservationDto expected1 = new ReservationDto("id1", "customerId", "eventId1");
        ReservationDto expected2 = new ReservationDto("id2", "customerId", "eventId2");

        List<ReservationDto> reservationDtos = List.of(expected1, expected2);

        when(reservationService.findByCustomer(any(UsernamePasswordAuthenticationToken.class))).thenReturn(reservationDtos);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/personal")
                        .with(user(customer)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("id1")))
                .andExpect(jsonPath("$[0].eventId", is("eventId1")))
                .andExpect(jsonPath("$[0].customerId", is("customerId")))
                .andExpect(jsonPath("$[1].id", is("id2")))
                .andExpect(jsonPath("$[1].eventId", is("eventId2")))
                .andExpect(jsonPath("$[1].customerId", is("customerId")));
    }
}
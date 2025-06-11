package com.sourcesense.smart_event_platform.unit.service;

import com.sourcesense.smart_event_platform.exception.EventNotFoundException;
import com.sourcesense.smart_event_platform.exception.FullEventException;
import com.sourcesense.smart_event_platform.exception.NoReservationPresentException;
import com.sourcesense.smart_event_platform.mapper.EventMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.persistance.EventRepository;
import com.sourcesense.smart_event_platform.configuration.Role;
import com.sourcesense.smart_event_platform.service.implementation.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;


    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        event = new Event("eventId", "eventName", "eventDescription", 20, LocalDateTime.of(2026, 11, 9, 21, 30), false);
        eventDto = new EventDto("eventId", "eventName", "eventDescription", 0, 20, LocalDateTime.of(2026, 11, 9, 21, 30), "username", false);
    }

    @Test
    void insert_Success() {
        //given
        InsertEventRequest insertEventRequest = new InsertEventRequest("eventName", "eventDescription", 20, LocalDateTime.of(2026, 11, 9, 21, 30), false);
        Customer customer = new Customer("customerId", "username", "password", "firstName", "lastName", Role.USER);
        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(customer, null);
        when(eventMapper.fromInsertRequestToModel(insertEventRequest)).thenReturn(event);
        when(eventMapper.fromModelToDto(event)).thenReturn(eventDto);
        //when
        EventDto result = eventService.insert(insertEventRequest, upat);
        //then

        assertEquals("eventId", result.id());
        assertEquals("eventName", result.name());
        assertEquals("eventDescription", result.description());
        assertEquals(0, result.actualCapacity());
        assertEquals(20, result.maxCapacity());
        assertEquals(LocalDateTime.of(2026, 11, 9, 21, 30), result.dateTime());
        assertFalse(result.isPrivate());

        verify(eventMapper).fromInsertRequestToModel(insertEventRequest);
        verify(eventRepository).save(event);


    }


    @Test
    void delete_Success() {
        //given
        when(eventRepository.findById("eventId")).thenReturn(Optional.of(event));
        when(eventRepository.existsById("eventId")).thenReturn(false);

        //when
        Boolean result = eventService.delete("eventId");

        //then
        assertTrue(result);

        verify(eventRepository).delete(event);
        verify(eventRepository).existsById("eventId");
    }

    @Test
    void delete_Failed() {
        //given
        when(eventRepository.findById("eventId")).thenReturn(Optional.empty());

        //then
        assertThrows(EventNotFoundException.class, () -> eventService.delete("eventId"));
    }

    @Test
    void update() {
    }

    @Test
    void findAll() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        List<Event> eventList = List.of(
                new Event("eventId1", "name1", "description1", 20, LocalDateTime.of(2025, 9, 21, 21, 30), false),
                new Event("eventId2", "name2", "description2", 30, LocalDateTime.of(2025, 8, 20, 21, 30), true),
                new Event("eventId3", "name3", "description3", 25, LocalDateTime.of(2025, 7, 19, 21, 30), false)
        );

        List<EventDto> eventDtoList = List.of(
                new EventDto("eventId1", "name1", "description1", 0, 20, LocalDateTime.of(2025, 9, 21, 21, 30), "customer1", false),
                new EventDto("eventId2", "name2", "description2", 0, 30, LocalDateTime.of(2025, 8, 20, 21, 30), "customer2", true),
                new EventDto("eventId3", "name3", "description3", 0, 25, LocalDateTime.of(2025, 7, 19, 21, 30), "customer3", false)
        );

        PageImpl<EventDto> dtoPage = new PageImpl<>(eventDtoList, pageable, eventDtoList.size());

        when(eventRepository.findAll()).thenReturn(eventList);
        when(eventMapper.fromListOfModelToDto(eventList)).thenReturn(eventDtoList);

        //when
        Page<EventDto> customerDtoPage = eventService.findAll(0, 5);

        //then
        assertEquals(dtoPage.getTotalElements(), customerDtoPage.getTotalElements());
        assertEquals(dtoPage.getTotalPages(), customerDtoPage.getTotalPages());
    }

    @Test
    void addReservation_Success() {
        //given
        when(eventRepository.findById("eventId")).thenReturn(Optional.of(event));
        //when
        Boolean result = eventService.addReservation("eventId");
        //then
        assertTrue(result);
        assertEquals(1, event.getActualCapacity());

        verify(eventRepository).save(event);
    }

    @Test
    void addReservation_FailedIfEventNotFound() {
        //given
        when(eventRepository.findById("eventId")).thenReturn(Optional.empty());
        //then
        assertThrows(EventNotFoundException.class, () -> eventService.addReservation("eventId"));
    }

    @Test
    void addReservation_FailedIfEventIsFull() {
        //given
        event.setActualCapacity(20);
        when(eventRepository.findById("eventId")).thenReturn(Optional.of(event));
        //then
        assertThrows(FullEventException.class, () -> eventService.addReservation("eventId"));
    }

    @Test
    void removeReservation_Success() {
        //given
        event.setActualCapacity(10);
        when(eventRepository.findById("eventId")).thenReturn(Optional.of(event));
        //when
        eventService.removeReservation("eventId");
        //then
        assertEquals(9, event.getActualCapacity());

        verify(eventRepository).save(event);
    }

    @Test
    void removeReservation_Failed() {
        //given
        event.setActualCapacity(0);
        when(eventRepository.findById("eventId")).thenReturn(Optional.of(event));
        //then
        assertThrows(NoReservationPresentException.class, () -> eventService.removeReservation("eventId"));
    }

    @Test
    void findById() {
    }

    @Test
    void findByDate() {
    }
}
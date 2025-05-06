package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.mapper.EventMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.persistance.CustomerRepository;
import com.sourcesense.smart_event_platform.persistance.EventRepository;
import com.sourcesense.smart_event_platform.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventMapper eventMapper;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void insert() {
        Customer customer = new Customer("customerId", "username", "password", "firstName", "lastName", Role.USER);
        InsertEventRequest insertEventRequest = new InsertEventRequest("eventName", "eventDescription", 20, LocalDateTime.of(2025, 11, 9, 8, 0), false);
        Event event = new Event("eventId", "eventName", "eventDescription", 20, LocalDateTime.of(2025, 11, 9, 8, 0), false);
        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(customer, null);
        EventDto result = new EventDto("eventId", "eventName", "eventDescription", 0, 20, LocalDateTime.of(2025, 11, 9, 8, 0), customer.getUsername(), false);
        when(eventMapper.fromInsertRequestToModel(insertEventRequest)).thenReturn(event);
        when(eventMapper.fromModelToDto(event)).thenReturn(result);

        //when
        EventDto eventDto = eventService.insert(insertEventRequest, upat);

        //then
        assertNotNull(eventDto);
        assertEquals("eventId", eventDto.id());
        assertEquals("eventName", result.name());
        assertEquals("eventDescription", eventDto.description());
        assertEquals(20, result.maxCapacity());
        assertEquals(LocalDateTime.of(2025, 11, 9, 8, 0), eventDto.dateTime());
        assertEquals("username", eventDto.organizerUsername());
        assertFalse(eventDto.isPrivate());


    }

    @Test
    void delete() {
        String eventId = "eventId";
        Event event = new Event("eventId", "eventName", "eventDescription", 20, LocalDateTime.of(2025, 11, 9, 8, 0), false);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.existsById(eventId)).thenReturn(false);
        //when
        Boolean result = eventService.delete(eventId);

        assertTrue(result);
    }

    @Test
    void update() {
    }

    @Test
    void findAll() {
        Event event1 = new Event("eventId1", "eventName1", "eventDescription1", 20, LocalDateTime.of(2025, 11, 9, 8, 0), false);
        Event event2 = new Event("eventId2", "eventName2", "eventDescription2", 30, LocalDateTime.of(2025, 9, 22, 8, 0), true);
        List<Event> eventList = List.of(event1, event2);
    }

    @Test
    void findById() {
    }

    @Test
    void findByDate() {
    }
}
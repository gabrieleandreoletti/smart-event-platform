package com.sourcesense.smart_event_platform.controller.definition;

import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.EventFilter;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateEventRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.List;

public interface EventController {

    EventDto insert(InsertEventRequest request, UsernamePasswordAuthenticationToken upat);

    Boolean delete(String eventID);

    Page<EventDto> findAll(int page, int size);

    List<String> getWaitlist(String eventId);

    Page<EventDto> getFilteredEvents(EventFilter filter);

    EventDto update(String eventId, UpdateEventRequest updateEventRequest);

    EventDto findByID(String eventID);

    List<EventDto> findByDate(LocalDate dateTime);
}

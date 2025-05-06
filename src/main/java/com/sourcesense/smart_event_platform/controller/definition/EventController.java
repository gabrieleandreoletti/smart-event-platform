package com.sourcesense.smart_event_platform.controller.definition;

import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateEventRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;

public interface EventController {

    EventDto insert(InsertEventRequest request, UsernamePasswordAuthenticationToken upat);

    Boolean delete(String eventID);

    List<EventDto> findAll();

    EventDto update(String eventId, UpdateEventRequest updateEventRequest);

    EventDto findByID(String eventID);

    List<EventDto> findByDate(LocalDateTime dateTime);
}

package com.sourcesense.smart_event_platform.service.definition;

import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateEventRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventDto insert(InsertEventRequest insertRequest, UsernamePasswordAuthenticationToken upat);

    Boolean delete(String eventID);

    EventDto update(String eventId, UpdateEventRequest eventRequest);

    Page<EventDto> findAll(int page, int size);

    Boolean addReservation(String eventId);

    void removeReservation(String eventId);

    EventDto findById(String eventID);

    List<EventDto> findByDate(LocalDateTime date);
}

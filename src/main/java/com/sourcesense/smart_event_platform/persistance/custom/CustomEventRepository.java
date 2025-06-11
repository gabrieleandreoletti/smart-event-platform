package com.sourcesense.smart_event_platform.persistance.custom;

import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.model.dto.request.EventFilter;

import java.time.LocalDate;
import java.util.List;

public interface CustomEventRepository {

    List<Event> getEventByFilter(EventFilter filter);

    List<Event> findByDate(LocalDate date);
}

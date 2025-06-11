package com.sourcesense.smart_event_platform.mapper;

import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event fromInsertRequestToModel(InsertEventRequest request);

    EventDto fromModelToDto(Event event);


    List<EventDto> fromListOfModelToDto(List<Event> events);

}

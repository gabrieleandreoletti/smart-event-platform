package com.sourcesense.smart_event_platform.controller.implementation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sourcesense.smart_event_platform.controller.definition.EventController;
import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateEventRequest;
import com.sourcesense.smart_event_platform.service.definition.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
public class EventControllerImpl implements EventController {

    private final EventService eventService;

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    public EventDto insert(@RequestBody @Valid InsertEventRequest request, UsernamePasswordAuthenticationToken upat) {
        return eventService.insert(request, upat);
    }

    @Override
    @DeleteMapping("/{eventID}")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    public Boolean delete(@PathVariable String eventID) {
        return eventService.delete(eventID);
    }

    @Override
    @GetMapping
    public List<EventDto> findAll() {
        return eventService.findAll();
    }

    @Override
    @PutMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    public EventDto update(@PathVariable String eventId, @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        return eventService.update(eventId, updateEventRequest);
    }

    @Override
    @GetMapping("/{eventID}")
    public EventDto findByID(@PathVariable String eventID) {
        return eventService.findById(eventID);
    }

    @Override
    @GetMapping("/date/{dateTime}")
    public List<EventDto> findByDate(@DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm") @PathVariable LocalDateTime dateTime) {
        return eventService.findByDate(dateTime);
    }
}

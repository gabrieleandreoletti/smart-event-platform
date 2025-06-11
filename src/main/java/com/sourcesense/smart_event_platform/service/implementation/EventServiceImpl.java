package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.exception.EventNotFoundException;
import com.sourcesense.smart_event_platform.exception.NoReservationPresentException;
import com.sourcesense.smart_event_platform.listener.event.DeleteEventEvent;
import com.sourcesense.smart_event_platform.mapper.EventMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.EventFilter;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateEventRequest;
import com.sourcesense.smart_event_platform.persistance.EventRepository;
import com.sourcesense.smart_event_platform.service.definition.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @CacheEvict(value = "events", allEntries = true)
    public EventDto insert(InsertEventRequest insertRequest, UsernamePasswordAuthenticationToken upat) {
        Event event = eventMapper.fromInsertRequestToModel(insertRequest);
        Customer organizer = (Customer) upat.getPrincipal();

        event.setOrganizerUsername(organizer.getUsername());

        eventRepository.save(event);

        return eventMapper.fromModelToDto(event);
    }

    @Override
    @CacheEvict(value = "events", allEntries = true)
    @Operation(
            summary = "delete of event"
    )
    public Boolean delete(String eventId) {
        Event event = getEventById(eventId);
        eventRepository.delete(event);

        applicationEventPublisher.publishEvent(new DeleteEventEvent(eventId));
        return !eventRepository.existsById(eventId);
    }

    @Override
    @CachePut(value = "events", key = "#eventId")
    public EventDto update(String eventId, UpdateEventRequest eventRequest) {
        Event event = getEventById(eventId);
        if (eventRequest.name() != null) {
            event.setName(eventRequest.name());
        }

        if (eventRequest.description() != null) {
            event.setDescription(eventRequest.description());
        }

        if (eventRequest.maxCapacity() != null) {
            event.setMaxCapacity(eventRequest.maxCapacity());
        }

        if (eventRequest.dateTime() != null) {
            event.setDateTime(eventRequest.dateTime());
        }

        eventRepository.save(event);

        return eventMapper.fromModelToDto(event);
    }

    @Override
    @Cacheable(value = "events", key = "'page_'+ #page + 'size_' + #size")
    public Page<EventDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventList = eventRepository.findAll(pageable);
        List<EventDto> eventDtoList = eventMapper.fromListOfModelToDto(eventList.toList());
        return new PageImpl<>(eventDtoList, pageable, eventDtoList.size());
    }

    @Override
    public List<String> getWaitlist(String eventId) {
        Event event = getEventById(eventId);
        return event.getWaitList();
    }

    @Override
    public Page<EventDto> getFilteredEvents(EventFilter filter) {
        Pageable pageable = PageRequest.of(filter.page(), filter.size());
        List<Event> eventList = eventRepository.getEventByFilter(filter);
        List<EventDto> eventDtoList = eventMapper.fromListOfModelToDto(eventList);
        return new PageImpl<>(eventDtoList, pageable, eventDtoList.size());
    }

    @Override
    @Cacheable(value = "events", key = "#eventId")
    public EventDto findById(String eventId) {
        Event event = getEventById(eventId);
        return eventMapper.fromModelToDto(event);
    }

    @Override
    @Cacheable(value = "events", key = "#date")
    public List<EventDto> findByDate(LocalDate date) {
        List<Event> eventList = eventRepository.findByDate(date);
        return eventMapper.fromListOfModelToDto(eventList);
    }

    @Override
    @CachePut(value = "events", key = "#eventId")
    public Boolean addReservation(String eventId) {
        Event event = getEventById(eventId);
        boolean isFull = event.getActualCapacity() >= event.getMaxCapacity();
        if (isFull) {
            return false;
        }
        event.setActualCapacity(event.getActualCapacity() + 1);
        eventRepository.save(event);
        return true;
    }

    @Override
    @CachePut(value = "events", key = "#eventId")
    public void removeReservation(String eventId) {
        Event event = getEventById(eventId);
        if (event.getActualCapacity() > 0) {
            event.setActualCapacity(event.getActualCapacity() - 1);
            eventRepository.save(event);
        } else {
            throw new NoReservationPresentException("There are no reservations");
        }
    }

    private Event getEventById(String id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("There is no event with ID " + id));
    }
}

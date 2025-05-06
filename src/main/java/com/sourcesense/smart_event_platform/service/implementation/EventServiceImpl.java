package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.exception.EventNotFoundException;
import com.sourcesense.smart_event_platform.exception.FullEventException;
import com.sourcesense.smart_event_platform.exception.NoReservationPresentException;
import com.sourcesense.smart_event_platform.mapper.EventMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.model.dto.EventDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertEventRequest;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateEventRequest;
import com.sourcesense.smart_event_platform.persistance.EventRepository;
import com.sourcesense.smart_event_platform.service.definition.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @CachePut(value = "events", key = "#result.id")
    public EventDto insert(InsertEventRequest insertRequest, UsernamePasswordAuthenticationToken upat) {
        Event event = eventMapper.fromInsertRequestToModel(insertRequest);
        Customer organizer = (Customer) upat.getPrincipal();

        event.setOrganizerUsername(organizer.getUsername());

        eventRepository.save(event);

        return eventMapper.fromModelToDto(event);
    }

    @Override
    @CacheEvict(value = "events", key = "#eventId")
    public Boolean delete(String eventId) {
        Event event = getEventById(eventId);
        eventRepository.delete(event);

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
    @Cacheable(value = "events", key = "'all'")
    public Page<EventDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventList = eventRepository.findAll(pageable);
        return eventMapper.fromPageOfModelToDto(eventList);
    }

    @Override
    public Boolean addReservation(String eventId) {
        Event event = getEventById(eventId);
        boolean isFull = event.getActualCapacity() >= event.getMaxCapacity();
        if (isFull) {
            throw new FullEventException("Event with id " + eventId + " is full");

        }
        event.setActualCapacity(event.getActualCapacity() + 1);
        eventRepository.save(event);
        return true;
    }

    @Override
    public void removeReservation(String eventId) {
        Event event = getEventById(eventId);
        if (event.getActualCapacity() > 0) {
            event.setActualCapacity(event.getActualCapacity() - 1);
        } else {
            throw new NoReservationPresentException("There are no reservations");
        }
    }

    @Override
    @Cacheable(value = "events", key = "#eventId")
    public EventDto findById(String eventId) {
        Event event = getEventById(eventId);
        return eventMapper.fromModelToDto(event);
    }

    @Override
    @Cacheable(value = "events", key = "#date")
    public List<EventDto> findByDate(LocalDateTime date) {
        List<Event> eventList = eventRepository.findByDateTime(date);
        return eventMapper.fromListOfModelToDto(eventList);
    }

    private Event getEventById(String id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("There is no event with ID " + id));
    }
}

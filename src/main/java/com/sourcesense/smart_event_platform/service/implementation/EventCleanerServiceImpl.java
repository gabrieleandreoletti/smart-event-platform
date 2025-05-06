package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.persistance.EventRepository;
import com.sourcesense.smart_event_platform.service.definition.EventCleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventCleanerServiceImpl implements EventCleanerService {

    private final EventRepository eventRepository;

    @Override
    public void cleanUpExpiredEvents() {
        List<Event> expiredEvents = eventRepository.findByDateTimeBefore(LocalDateTime.now());

        if (!expiredEvents.isEmpty()) {
            eventRepository.deleteAll(expiredEvents);
            log.info("Deleted expired events");
        } else {
            log.info("There are no elements to be eliminated");
        }
    }
}

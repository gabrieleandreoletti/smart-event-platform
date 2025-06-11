package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.listener.event.DeleteEventEvent;
import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.persistance.EventRepository;
import com.sourcesense.smart_event_platform.service.definition.EventCleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventCleanerServiceImpl implements EventCleanerService {

    private final EventRepository eventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @CacheEvict(value = "events", allEntries = true)
    public void cleanUpExpiredEvents() {
        List<Event> expiredEvents = eventRepository.findByDateTimeBefore(LocalDateTime.now().minusDays(7));
        if (!expiredEvents.isEmpty()) {
            eventRepository.deleteAll(expiredEvents);
            for (Event event : expiredEvents) {
                applicationEventPublisher.publishEvent(new DeleteEventEvent(event.getId()));
            }
            log.info("Deleted expired events");
        } else {
            log.info("There are no elements to be eliminated");
        }
    }
}

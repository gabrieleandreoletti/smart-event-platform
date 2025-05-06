package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.exception.DuplicateWaitlistException;
import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.persistance.EventRepository;
import com.sourcesense.smart_event_platform.service.definition.WaitlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitlistServiceImpl implements WaitlistService {

    private final EventRepository eventRepository;

    @Override
    public void addToWaitList(Event event, String customerId) {
        if (!event.getWaitList().contains(customerId)) {
            event.getWaitList().add(customerId);
            eventRepository.save(event);
            log.info("{} aggiunto alla waitlist dell'evento {}", customerId, event.getId());
        } else {
            throw new DuplicateWaitlistException("this user is already in waitlist");
        }
    }

    @Override
    public String handlePromotion(Event event) {
        if (event.getWaitList().isEmpty()) {
            return null;
        }
        String nextCustomer = event.getWaitList().removeFirst();
        eventRepository.save(event);
        log.info("{} ha ottenuto il posto per l'evento {}", nextCustomer, event.getId());
        return nextCustomer;
    }
}

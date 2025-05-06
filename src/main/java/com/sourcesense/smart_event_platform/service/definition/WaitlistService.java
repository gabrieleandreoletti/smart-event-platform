package com.sourcesense.smart_event_platform.service.definition;

import com.sourcesense.smart_event_platform.model.Event;

public interface WaitlistService {
    void addToWaitList(Event event, String customerId);

    public String handlePromotion(Event event);
}

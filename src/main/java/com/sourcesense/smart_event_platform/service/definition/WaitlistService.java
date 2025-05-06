package com.sourcesense.smart_event_platform.service.definition;

public interface WaitlistService {
    void addToWaitList(String eventId, String customerId);

    public String handlePromotion(String eventId);
}

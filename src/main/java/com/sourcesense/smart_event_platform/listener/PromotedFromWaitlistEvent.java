package com.sourcesense.smart_event_platform.listener;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class PromotedFromWaitlistEvent extends ApplicationEvent {

    private String customerId;
    private String eventId;

    public PromotedFromWaitlistEvent(String customerId, String eventId) {
        super(eventId);
        this.customerId = customerId;
        this.eventId = eventId;
    }
}

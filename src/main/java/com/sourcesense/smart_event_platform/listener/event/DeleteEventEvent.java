package com.sourcesense.smart_event_platform.listener.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeleteEventEvent extends ApplicationEvent {

    private final String eventId;

    public DeleteEventEvent(String eventId) {
        super(eventId);
        this.eventId = eventId;
    }
}

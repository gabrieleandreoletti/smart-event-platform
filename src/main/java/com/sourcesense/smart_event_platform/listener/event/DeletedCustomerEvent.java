package com.sourcesense.smart_event_platform.listener.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeletedCustomerEvent extends ApplicationEvent {

    private final String customerId;

    public DeletedCustomerEvent(String customerId) {
        super(customerId);
        this.customerId = customerId;
    }
}

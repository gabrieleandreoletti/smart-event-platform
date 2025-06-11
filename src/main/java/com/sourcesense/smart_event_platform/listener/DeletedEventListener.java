package com.sourcesense.smart_event_platform.listener;

import com.sourcesense.smart_event_platform.listener.event.DeleteEventEvent;
import com.sourcesense.smart_event_platform.service.definition.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletedEventListener {

    private final ReservationService reservationService;

    @EventListener
    public void deleteConnectedReservation(DeleteEventEvent event) {
        reservationService.deleteByEventId(event.getEventId());
    }

}

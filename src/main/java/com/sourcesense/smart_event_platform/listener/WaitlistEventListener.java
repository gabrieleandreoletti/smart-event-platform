package com.sourcesense.smart_event_platform.listener;

import com.sourcesense.smart_event_platform.model.dto.request.InsertReservationRequest;
import com.sourcesense.smart_event_platform.service.definition.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitlistEventListener {
    private final ReservationService reservationService;

    @EventListener
    public void handlePromotion(PromotedFromWaitlistEvent event) {
        InsertReservationRequest request = new InsertReservationRequest(event.getEventId());

        reservationService.insertFromWaitlist(event.getEventId());
    }
}

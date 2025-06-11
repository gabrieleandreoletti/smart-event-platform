package com.sourcesense.smart_event_platform.listener;

import com.sourcesense.smart_event_platform.listener.event.DeletedCustomerEvent;
import com.sourcesense.smart_event_platform.service.definition.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletedCustomerEventListener {

    private final ReservationService reservationService;

    public void deleteConnectedReservation(DeletedCustomerEvent customerEvent) {
        reservationService.deleteByCustomerId(customerEvent.getCustomerId());
    }
}

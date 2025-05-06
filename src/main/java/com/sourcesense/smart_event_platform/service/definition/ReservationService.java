package com.sourcesense.smart_event_platform.service.definition;

import com.sourcesense.smart_event_platform.model.dto.ReservationDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertReservationRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

public interface ReservationService {

    ReservationDto insert(InsertReservationRequest insertReservationRequest, UsernamePasswordAuthenticationToken upat);

    Boolean delete(String reservationID);

    ReservationDto insertFromWaitlist(String eventId);

    List<ReservationDto> findAll();

    ReservationDto findById(String reservationId);

    List<ReservationDto> findByCustomer(String customerId);

}

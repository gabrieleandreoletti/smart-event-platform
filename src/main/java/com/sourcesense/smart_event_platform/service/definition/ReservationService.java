package com.sourcesense.smart_event_platform.service.definition;

import com.sourcesense.smart_event_platform.model.dto.ReservationDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertReservationRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

public interface ReservationService {

    ReservationDto insert(InsertReservationRequest insertReservationRequest, UsernamePasswordAuthenticationToken upat);

    Boolean delete(String reservationID);

    void insertFromWaitlist(String eventId);

    Page<ReservationDto> findAll(int page, int size);

    ReservationDto findById(String reservationId);

    void deleteByEventId(String eventId);

    void deleteByCustomerId(String customerId);

    List<ReservationDto> findByCustomer(UsernamePasswordAuthenticationToken upat);

}

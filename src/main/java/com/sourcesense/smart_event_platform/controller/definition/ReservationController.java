package com.sourcesense.smart_event_platform.controller.definition;

import com.sourcesense.smart_event_platform.model.dto.ReservationDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertReservationRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

public interface ReservationController {
    ReservationDto insert(InsertReservationRequest insertReservationRequest, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken);

    Boolean delete(String reservationId);

    Page<ReservationDto> findAll(int page, int size);

    ReservationDto findById(String reservationId);

    List<ReservationDto> findByCustomer(UsernamePasswordAuthenticationToken upat);


}

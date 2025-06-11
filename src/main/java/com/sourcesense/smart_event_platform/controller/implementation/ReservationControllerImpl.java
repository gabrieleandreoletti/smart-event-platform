package com.sourcesense.smart_event_platform.controller.implementation;


import com.sourcesense.smart_event_platform.controller.definition.ReservationController;
import com.sourcesense.smart_event_platform.model.dto.ReservationDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertReservationRequest;
import com.sourcesense.smart_event_platform.service.definition.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reservations")
public class ReservationControllerImpl implements ReservationController {

    private final ReservationService reservationService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDto insert(@RequestBody @Valid InsertReservationRequest insertReservationRequest, UsernamePasswordAuthenticationToken upat) {
        return reservationService.insert(insertReservationRequest, upat);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Boolean delete(@PathVariable String id) {
        return reservationService.delete(id);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    public Page<ReservationDto> findAll(@RequestParam int page, @RequestParam int size) {
        return reservationService.findAll(page, size);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    public ReservationDto findById(@PathVariable String id) {
        return reservationService.findById(id);
    }

    @Override
    @GetMapping("/personal")
    @PreAuthorize("hasRole('USER')")
    public List<ReservationDto> findByCustomer(UsernamePasswordAuthenticationToken upat) {
        return reservationService.findByCustomer(upat);
    }
}

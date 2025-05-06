package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.exception.DuplicateWaitlistException;
import com.sourcesense.smart_event_platform.exception.ReservationNotFoundException;
import com.sourcesense.smart_event_platform.listener.PromotedFromWaitlistEvent;
import com.sourcesense.smart_event_platform.mapper.ReservationMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.Reservation;
import com.sourcesense.smart_event_platform.model.dto.ReservationDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertReservationRequest;
import com.sourcesense.smart_event_platform.persistance.ReservationRepository;
import com.sourcesense.smart_event_platform.service.definition.EventService;
import com.sourcesense.smart_event_platform.service.definition.ReservationService;
import com.sourcesense.smart_event_platform.service.definition.WaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventService eventService;
    private final ReservationMapper reservationMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final WaitlistService waitlistService;


    @Override
    @Transactional
    @CachePut(value = "reservations", key = "#result.id")
    public ReservationDto insert(InsertReservationRequest insertReservationRequest, UsernamePasswordAuthenticationToken upat) {
        Customer customer = (Customer) upat.getPrincipal();
        boolean exist = reservationRepository.existsByCustomerIdAndEventId(customer.getId(), insertReservationRequest.eventId());
        if (exist) {
            throw new DuplicateWaitlistException("L'utente è già registrato nell'evento");
        }

        Reservation reservation = reservationMapper.fromInsertRequestToModel(insertReservationRequest);
        boolean isAvailable = eventService.addReservation(insertReservationRequest.eventId());
        if (isAvailable) {
            reservation.setCustomerId(customer.getId());
            reservationRepository.save(reservation);
        } else {
            waitlistService.addToWaitList(reservation.getEventId(), customer.getId());
        }
        return reservationMapper.fromModelToDto(reservation);

    }

    @Override
    @Transactional
    @CacheEvict(value = "reservations", key = "reservationId")
    public Boolean delete(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException("There is no reservation with id " + reservationId));
        String eventId = reservation.getEventId();
        eventService.removeReservation(eventId);
        reservationRepository.delete(reservation);
        String nextCustomerId = waitlistService.handlePromotion(eventId);
        applicationEventPublisher.publishEvent(new PromotedFromWaitlistEvent(nextCustomerId, eventId));
        return !reservationRepository.existsById(reservationId);
    }

    @Override
    @CachePut(value = "reservations", key = "#result.id")
    public void insertFromWaitlist(String eventId) {
        String customerId = waitlistService.handlePromotion(eventId);

        Reservation reservation = new Reservation(eventId, customerId);
        reservationRepository.save(reservation);

        eventService.addReservation(eventId);
    }

    @Override
    @Cacheable(value = "reservations", key = "'all'")
    public List<ReservationDto> findAll() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservationMapper.fromListOfModelToDto(reservations);
    }

    @Override
    @Cacheable(value = "reservations", key = "#reservationId")
    public ReservationDto findById(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException("There is no reservation with id " + reservationId));
        return reservationMapper.fromModelToDto(reservation);
    }

    @Override
    public List<ReservationDto> findByCustomer(UsernamePasswordAuthenticationToken upat) {
        Customer customer = (Customer) upat.getPrincipal();
        List<Reservation> reservations = reservationRepository.findByCustomerId(customer.getId());
        return reservationMapper.fromListOfModelToDto(reservations);
    }
}

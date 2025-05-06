package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.listener.PromotedFromWaitlistEvent;
import com.sourcesense.smart_event_platform.exception.EventNotFoundException;
import com.sourcesense.smart_event_platform.exception.ReservationNotFoundException;
import com.sourcesense.smart_event_platform.mapper.ReservationMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.model.Reservation;
import com.sourcesense.smart_event_platform.model.dto.ReservationDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertReservationRequest;
import com.sourcesense.smart_event_platform.persistance.EventRepository;
import com.sourcesense.smart_event_platform.persistance.ReservationRepository;
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
    private final EventRepository eventRepository;
    private final ReservationMapper reservationMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final WaitlistService waitlistService;


    @Override
    @Transactional
    @CachePut(value = "reservations", key = "#result.id")
    public ReservationDto insert(InsertReservationRequest insertReservationRequest, UsernamePasswordAuthenticationToken upat) {
        Customer customer = (Customer) upat.getPrincipal();
        Event event = eventRepository.findById(insertReservationRequest.eventId()).orElseThrow(() -> new EventNotFoundException("There is no eventi with id : " + insertReservationRequest.eventId()));
        Reservation reservation = reservationMapper.fromInsertRequestToModel(insertReservationRequest);
        if (!event.isFull()) {
            event.addReservation();
            reservation.setCustomerId(customer.getId());
            reservationRepository.save(reservation);
            eventRepository.save(event);
        } else {
            waitlistService.addToWaitList(event, customer.getId());
        }
        return reservationMapper.fromModelToDto(reservation);
    }

    @Override
    @Transactional
    @CacheEvict(value = "reservations", key = "reservationId")
    public Boolean delete(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException("There is no reservation with id " + reservationId));
        Event event = eventRepository.findById(reservation.getEventId()).orElseThrow(() -> new EventNotFoundException("There is no event with id " + reservation.getEventId()));
        event.removeReservation();
        reservationRepository.delete(reservation);

        if (!event.getWaitList().isEmpty()) {
            String nextCustomerId = waitlistService.handlePromotion(event);
            applicationEventPublisher.publishEvent(new PromotedFromWaitlistEvent(nextCustomerId, event.getId()));
        }
        eventRepository.save(event);
        return !reservationRepository.existsById(reservationId);
    }

    @Override
    @CachePut(value = "reservations", key = "#result.id")
    public ReservationDto insertFromWaitlist(String eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("event with id " + eventId + " not found"));
        String customerId = waitlistService.handlePromotion(event);

        Reservation reservation = new Reservation(eventId, customerId);
        reservationRepository.save(reservation);

        event.addReservation();
        eventRepository.save(event);

        return reservationMapper.fromModelToDto(reservation);
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
    @Cacheable(value = "reservations", key = "#customerId")
    public List<ReservationDto> findByCustomer(String customerId) {
        List<Reservation> reservations = reservationRepository.findByCustomerId(customerId);
        return reservationMapper.fromListOfModelToDto(reservations);
    }
}

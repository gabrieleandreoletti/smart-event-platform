package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.exception.DuplicateWaitlistException;
import com.sourcesense.smart_event_platform.exception.ReservationNotFoundException;
import com.sourcesense.smart_event_platform.listener.event.PromotedFromWaitlistEvent;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @CacheEvict(value = "reservations", allEntries = true)
    public ReservationDto insert(InsertReservationRequest insertReservationRequest, UsernamePasswordAuthenticationToken upat) {
        Customer customer = (Customer) upat.getPrincipal();
        boolean exist = reservationRepository.existsByCustomerIdAndEventId(customer.getId(), insertReservationRequest.eventId());
        if (exist) {
            throw new DuplicateWaitlistException("The user is already registered in the event");
        }
        Reservation reservation = reservationMapper.fromInsertRequestToModel(insertReservationRequest);
        reservation.setCustomerId(customer.getId());
        boolean isAvailable = eventService.addReservation(insertReservationRequest.eventId());
        if (isAvailable) {
            reservationRepository.save(reservation);
        } else {
            waitlistService.addToWaitList(reservation.getEventId(), customer.getId());
        }
        return reservationMapper.fromModelToDto(reservation);
    }

    @Override
    @Transactional
    @CacheEvict(value = "reservations", allEntries = true)
    public Boolean delete(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        String eventId = reservation.getEventId();
        eventService.removeReservation(eventId);
        reservationRepository.delete(reservation);
        String nextCustomerId = waitlistService.handlePromotion(eventId);
        applicationEventPublisher.publishEvent(new PromotedFromWaitlistEvent(nextCustomerId, eventId));
        return !reservationRepository.existsById(reservationId);
    }

    @Override
    public void insertFromWaitlist(String eventId) {
        String customerId = waitlistService.handlePromotion(eventId);
        Reservation reservation = new Reservation(eventId, customerId);
        reservationRepository.save(reservation);
        eventService.addReservation(eventId);
    }

    @Override
    @Cacheable(value = "reservations", key = "'page_'+ #pageNumber + 'size_' + #pageSize")
    public Page<ReservationDto> findAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Reservation> reservations = reservationRepository.findAll(pageable);
        List<ReservationDto> reservationDtos = reservationMapper.fromListOfModelToDto(reservations.toList());
        return new PageImpl<>(reservationDtos, pageable, reservationDtos.size());
    }

    @Override
    @Cacheable(value = "customers", key = "#reservationId")
    public ReservationDto findById(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        return reservationMapper.fromModelToDto(reservation);
    }


    @Override
    @Cacheable(value = "customers", key = "#upat.principal")
    public List<ReservationDto> findByCustomer(UsernamePasswordAuthenticationToken upat) {
        Customer customer = (Customer) upat.getPrincipal();
        List<Reservation> reservations = reservationRepository.findByCustomerId(customer.getId());
        return reservationMapper.fromListOfModelToDto(reservations);
    }

    //Listener Methods
    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public void deleteByEventId(String eventId) {
        reservationRepository.deleteByEventId(eventId);
    }

    @Override
    @CacheEvict(value = "reservations", allEntries = true)
    public void deleteByCustomerId(String customerId) {
        reservationRepository.deleteByCustomerId(customerId);
    }

    //Usato per evitare ripetizioni di codice
    private Reservation getReservationById(String id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("There is no reservation with id " + id));
    }
}

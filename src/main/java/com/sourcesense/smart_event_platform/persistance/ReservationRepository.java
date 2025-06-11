package com.sourcesense.smart_event_platform.persistance;

import com.sourcesense.smart_event_platform.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {

    List<Reservation> findByCustomerId(String id);

    List<Reservation> findByEventId(String id);

    Boolean existsByCustomerIdAndEventId(String customerId, String eventId);

    void deleteByEventId(String eventId);

    void deleteByCustomerId(String customerId);
}

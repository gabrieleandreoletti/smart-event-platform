package com.sourcesense.smart_event_platform.persistance;

import com.sourcesense.smart_event_platform.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findByDateTime(LocalDateTime dateTime);

    List<Event> findByDateTimeBefore(LocalDateTime now);
}

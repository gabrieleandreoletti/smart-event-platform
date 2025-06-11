package com.sourcesense.smart_event_platform.persistance.custom;

import com.sourcesense.smart_event_platform.model.Event;
import com.sourcesense.smart_event_platform.model.dto.request.EventFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomEventRepositoryImpl implements CustomEventRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Event> getEventByFilter(EventFilter filter) {
        Query query = new Query();

        if (filter.name() != null && !filter.name().isBlank()) {
            query.addCriteria(Criteria.where("name").regex(filter.name()));
        }

        if (filter.desc() != null && !filter.desc().isBlank()) {
            query.addCriteria(Criteria.where("desc").regex(filter.desc()));
        }

        if (filter.actualCapacity() != null) {
            query.addCriteria(Criteria.where("actualCapacity").is(filter.actualCapacity()));
        }

        if (filter.isPrivate() != null) {
            query.addCriteria(Criteria.where("isPrivate").is(filter.isPrivate()));
        }

        if (filter.dateTime() != null) {
            query.addCriteria(Criteria.where("name").is(filter.dateTime()));
        }

        return mongoTemplate.find(query, Event.class);
    }

    @Override
    public List<Event> findByDate(LocalDate date) {
        Query query = new Query();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        //che comando devo usare per trovare gli eventi che hanno una LocalDateTime come campo , pasando una local date
        query.addCriteria(Criteria.where("dateTime").gte(startOfDay).lte(endOfDay));

        return mongoTemplate.find(query, Event.class);


    }
}

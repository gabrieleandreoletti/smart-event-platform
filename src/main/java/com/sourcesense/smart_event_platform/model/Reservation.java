package com.sourcesense.smart_event_platform.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@CompoundIndex(def = "{'customerId': 1 , 'eventId' : 1 }",unique = true)
@Document("reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
    @Id
    private String id;

    private String customerId;
    private String eventId;

    @CreatedDate
    private LocalDateTime purchasedTime;

    public Reservation(String eventId, String customerId) {
        this.eventId = eventId;
        this.customerId = customerId;
    }
}

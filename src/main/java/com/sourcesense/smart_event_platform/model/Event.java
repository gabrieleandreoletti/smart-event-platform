package com.sourcesense.smart_event_platform.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("events")
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @Id
    private String id;

    private String name;
    private String description;
    private Integer maxCapacity;
    private Integer actualCapacity = 0;

    private LocalDateTime dateTime;

    private String organizerUsername;

    private List<String> waitList = new ArrayList<>();

    private boolean isPrivate;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    public Event(String id, String name, String description, Integer maxCapacity, LocalDateTime dateTime, Boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.maxCapacity = maxCapacity;
        this.isPrivate = isPrivate;
    }

}

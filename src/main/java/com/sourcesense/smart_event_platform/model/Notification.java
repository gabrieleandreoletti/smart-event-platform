package com.sourcesense.smart_event_platform.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("notifications")
@Getter
@Setter
public class Notification {
    @Id
    private String id;

    private String receiverId;

    private String message;

    private Boolean isRead;

    @CreatedDate
    private LocalDateTime createdAt;
}

package com.sourcesense.smart_event_platform.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("notifications")
@Getter
@Setter
public class Notification {
    @Id
    private String id;

    private String receiverID;

    private String message;

    private Boolean isRead;
}

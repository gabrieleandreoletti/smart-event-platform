package com.sourcesense.smart_event_platform.persistance;

import com.sourcesense.smart_event_platform.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByReceiverId(String reciverId);

}

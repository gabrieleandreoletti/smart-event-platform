package com.sourcesense.smart_event_platform.service.definition;

import com.sourcesense.smart_event_platform.model.dto.request.InsertNotificationRequest;

public interface NotificationService {

    void sendNotification(InsertNotificationRequest notificationRequest);
}

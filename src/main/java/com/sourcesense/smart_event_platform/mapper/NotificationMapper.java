package com.sourcesense.smart_event_platform.mapper;

import com.sourcesense.smart_event_platform.model.Notification;
import com.sourcesense.smart_event_platform.model.dto.request.InsertNotificationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification fromRequestToModel(InsertNotificationRequest insertNotificationRequest);
}

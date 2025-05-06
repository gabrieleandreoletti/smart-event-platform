package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.mapper.NotificationMapper;
import com.sourcesense.smart_event_platform.model.Notification;
import com.sourcesense.smart_event_platform.model.dto.request.InsertNotificationRequest;
import com.sourcesense.smart_event_platform.persistance.NotificationRepository;
import com.sourcesense.smart_event_platform.service.definition.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public void sendNotification(InsertNotificationRequest notificationRequest) {
        Notification notification = notificationMapper.fromRequestToModel(notificationRequest);
        notificationRepository.save(notification);
    }
}

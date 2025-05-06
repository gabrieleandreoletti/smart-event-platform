package com.sourcesense.smart_event_platform.scheduler;

import com.sourcesense.smart_event_platform.service.definition.EventCleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventCleanupScheduler {

    private final EventCleanerService cleanerService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void cleanUpEvents() {
        cleanerService.cleanUpExpiredEvents();
    }
}

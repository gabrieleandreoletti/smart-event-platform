package com.sourcesense.smart_event_platform.model.dto;

import java.time.LocalDateTime;

public record EventDto(
        String id,
        String name,
        String description,
        int actualCapacity,
        int maxCapacity,
        LocalDateTime dateTime,
        String organizerUsername,
        boolean isPrivate
) {
}

package com.sourcesense.smart_event_platform.model.dto;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
public record EventDto(
        String id,
        String name,
        String description,
        int actualCapacity,
        int maxCapacity,
        @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime dateTime,
        String organizerUsername,
        boolean isPrivate
) {
}

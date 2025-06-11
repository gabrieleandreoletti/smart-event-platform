package com.sourcesense.smart_event_platform.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record EventFilter(String name, String desc,
                          @Future LocalDateTime dateTime, Boolean isPrivate,
                          @Positive Integer actualCapacity,
                          @NotNull @Positive int page, @NotNull @Positive int size) {
}

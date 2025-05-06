package com.sourcesense.smart_event_platform.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record InsertEventRequest(
        @NotBlank @Size(min = 3, max = 25) String name,
        @NotBlank @Size(min = 5, max = 80) String description,
        @Min(5) int maxCapacity,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm") @Future LocalDateTime dateTime,
        boolean isPrivate
) {
}

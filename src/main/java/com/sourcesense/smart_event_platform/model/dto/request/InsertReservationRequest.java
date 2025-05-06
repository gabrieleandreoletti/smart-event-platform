package com.sourcesense.smart_event_platform.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record InsertReservationRequest(
        @NotBlank String eventId
) {
}

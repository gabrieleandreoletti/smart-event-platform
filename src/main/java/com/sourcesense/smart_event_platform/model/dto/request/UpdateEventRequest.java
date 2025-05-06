package com.sourcesense.smart_event_platform.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record UpdateEventRequest(@Size(min = 3, max = 25) String name,
                                 @Size(min = 5, max = 80) String description,
                                 @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm") LocalDateTime dateTime,
                                 @Min(5) Integer maxCapacity
) {
}

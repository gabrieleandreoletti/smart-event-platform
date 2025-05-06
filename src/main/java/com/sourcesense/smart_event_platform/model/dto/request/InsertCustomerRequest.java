package com.sourcesense.smart_event_platform.model.dto.request;

import com.sourcesense.smart_event_platform.security.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InsertCustomerRequest(
        @NotBlank @Size(min = 4, max = 16) String username,
        @NotBlank @Size(min = 4, max = 16) String password,
        @NotBlank @Size(min = 2, max = 16) String firstName,
        @NotBlank @Size(min = 2, max = 16) String lastName,
        Role role) {
}

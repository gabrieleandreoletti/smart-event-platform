package com.sourcesense.smart_event_platform.model.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(@Size(min = 4, max = 20) String firstName,
                                    @Size(min = 4, max = 20) String lastName,
                                    @Size(min = 4, max = 20) String username,
                                    @Size(min = 4, max = 20) String password
) {
}

package com.sourcesense.smart_event_platform.controller.definition;

import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertCustomerRequest;
import com.sourcesense.smart_event_platform.model.dto.request.LoginCustomerRequest;
import org.springframework.http.ResponseEntity;

public interface AuthController {
    CustomerDto registration(InsertCustomerRequest insertRequest);

    ResponseEntity<String> login(LoginCustomerRequest loginRequest);
}

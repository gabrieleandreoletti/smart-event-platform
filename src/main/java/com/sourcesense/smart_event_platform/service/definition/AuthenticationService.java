package com.sourcesense.smart_event_platform.service.definition;

import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertCustomerRequest;
import com.sourcesense.smart_event_platform.model.dto.request.LoginCustomerRequest;

public interface AuthenticationService {

    CustomerDto registration(InsertCustomerRequest insertRequest);


    String login(LoginCustomerRequest loginRequest);
}

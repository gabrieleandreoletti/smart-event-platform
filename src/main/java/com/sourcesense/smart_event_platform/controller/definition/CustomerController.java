package com.sourcesense.smart_event_platform.controller.definition;

import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateCustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface CustomerController {

    Boolean delete(String customerID);

    CustomerDto update(UpdateCustomerRequest updateRequest, UsernamePasswordAuthenticationToken token);

    CustomerDto findByID(String customerID);

    CustomerDto findByUsername(String username);

    Page<CustomerDto> findAll(int page, int size);
}

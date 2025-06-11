package com.sourcesense.smart_event_platform.service.definition;

import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateCustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface CustomerService {

    Boolean delete(String customerID);

    CustomerDto update(UpdateCustomerRequest updateRequest, UsernamePasswordAuthenticationToken token);

    Page<CustomerDto> findAll(int page, int size);

    CustomerDto findByID(String customerId);

    CustomerDto findByUsername(String username);
}

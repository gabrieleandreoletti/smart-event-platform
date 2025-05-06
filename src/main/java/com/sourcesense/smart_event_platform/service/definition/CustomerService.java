package com.sourcesense.smart_event_platform.service.definition;

import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateCustomerRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

public interface CustomerService {

    Boolean delete(String customerID);

    CustomerDto update(UpdateCustomerRequest updateRequest, UsernamePasswordAuthenticationToken token);

    List<CustomerDto> findAll();

    CustomerDto findByID(String customerId);

    CustomerDto findByUsername(String username);
}

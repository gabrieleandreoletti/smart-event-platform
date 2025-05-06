package com.sourcesense.smart_event_platform.controller.implementation;

import com.sourcesense.smart_event_platform.controller.definition.CustomerController;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateCustomerRequest;
import com.sourcesense.smart_event_platform.service.definition.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/customers")
public class CustomerControllerImpl implements CustomerController {

    private final CustomerService service;

    @Override
    @DeleteMapping("{customerID}")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean delete(@PathVariable String customerID) {
        return service.delete(customerID);
    }

    @Override
    @PutMapping
    public CustomerDto update(@RequestBody @Valid UpdateCustomerRequest updateRequest, UsernamePasswordAuthenticationToken token) {
        return service.update(updateRequest, token);
    }

    @Override
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDto findByID(@PathVariable String customerId) {
        return service.findByID(customerId);
    }

    @Override
    @GetMapping("/username/{username}")
    public CustomerDto findByUsername(@PathVariable String username) {
        return service.findByUsername(username);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerDto> findAll() {
        return service.findAll();
    }


}

package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.exception.InvalidCredentialsException;
import com.sourcesense.smart_event_platform.mapper.CustomerMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertCustomerRequest;
import com.sourcesense.smart_event_platform.model.dto.request.LoginCustomerRequest;
import com.sourcesense.smart_event_platform.persistance.CustomerRepository;
import com.sourcesense.smart_event_platform.configuration.Role;
import com.sourcesense.smart_event_platform.service.definition.AuthenticationService;
import com.sourcesense.smart_event_platform.utility.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;
    private final CustomerMapper customerMapper;

    @Override
    @CacheEvict(value = "customers", allEntries = true)
    public CustomerDto registration(InsertCustomerRequest insertRequest) {
        Customer customer = customerMapper.fromInsertRequestToModel(insertRequest);
        cryptPassword(customer);
        Role role = insertRequest.role() == null ? Role.USER : insertRequest.role();
        customer.setRole(role);
        customerRepository.save(customer);
        return customerMapper.fromModelToDto(customer);
    }

    @Override
    public String login(LoginCustomerRequest loginRequest) {
        Customer customer = customerRepository.findByUsername(loginRequest.username()).orElseThrow(() -> new InvalidCredentialsException("Incorrect access credentials"));
        if (passwordEncoder.matches(loginRequest.password(), customer.getPassword())) {
            return jwtUtility.generateToken(customer);
        } else {
            throw new InvalidCredentialsException("The inserted password does not correspond");
        }
    }

    private void cryptPassword(Customer customer) {
        String encryptedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encryptedPassword);
    }
}

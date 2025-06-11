package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.exception.CustomerNotFoundException;
import com.sourcesense.smart_event_platform.mapper.CustomerMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateCustomerRequest;
import com.sourcesense.smart_event_platform.persistance.CustomerRepository;
import com.sourcesense.smart_event_platform.service.definition.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @CacheEvict(value = "customers", allEntries = true)
    public Boolean delete(String customerId) {
        Customer customer = getCustomerById(customerId);
        customerRepository.delete(customer);
        return !customerRepository.existsByUsernameAndId(customer.getUsername(), customerId);
    }

    @Override
    @CachePut(value = "customers", key = "#result.id")
    public CustomerDto update(UpdateCustomerRequest updateRequest, UsernamePasswordAuthenticationToken authenticationToken) {

        Customer customer = (Customer) authenticationToken.getPrincipal();

        if (updateRequest.firstName() != null) {
            customer.setFirstName(updateRequest.firstName());
        }

        if (updateRequest.lastName() != null) {
            customer.setLastName(updateRequest.lastName());
        }

        if (updateRequest.username() != null) {
            customer.setUsername(updateRequest.username());
        }

        if (updateRequest.password() != null) {
            customer.setPassword(passwordEncoder.encode(updateRequest.password()));
        }

        customerRepository.save(customer);

        return customerMapper.fromModelToDto(customer);
    }

    @Override
    @Cacheable(value = "customers", key = "'page_'+ #pageNumber + 'size_' + #pageSize")
    public Page<CustomerDto> findAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Customer> customerList = customerRepository.findAll(pageable);
        List<CustomerDto> customerDtoList = customerMapper.fromListOfEntityToDto(customerList.toList());
        return new PageImpl<>(customerDtoList, pageable, customerDtoList.size());
    }

    @Override
    @Cacheable(value = "customers", key = "#customerId")
    public CustomerDto findByID(String customerId) {
        Customer customer = getCustomerById(customerId);
        return customerMapper.fromModelToDto(customer);
    }

    @Override
    @Cacheable(value = "customers", key = "#username")
    public CustomerDto findByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username).orElseThrow(() -> new CustomerNotFoundException("Non è presente un utente con username " + username));
        return customerMapper.fromModelToDto(customer);
    }

    private Customer getCustomerById(String id) {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Non è presente un utente con id " + id));
    }
}

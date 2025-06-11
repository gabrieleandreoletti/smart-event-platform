package com.sourcesense.smart_event_platform.unit.service;

import com.sourcesense.smart_event_platform.exception.CustomerNotFoundException;
import com.sourcesense.smart_event_platform.mapper.CustomerMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateCustomerRequest;
import com.sourcesense.smart_event_platform.persistance.CustomerRepository;
import com.sourcesense.smart_event_platform.configuration.Role;
import com.sourcesense.smart_event_platform.service.implementation.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomerMapper mapper;

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    Customer customer;
    CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customer = new Customer("customerId", "username", "password", "firstName", "lastName", Role.USER);
        customerDto = new CustomerDto("customerId", "username", "firstName lastName");
    }

    @Test
    void delete_Success() {
        //given
        when(repository.findById("customerId")).thenReturn(Optional.of(customer));
        when(repository.existsByUsernameAndId("username", "customerId")).thenReturn(false);
        //when
        Boolean success = customerService.delete("customerId");

        //then
        assertTrue(success);
        verify(repository).findById(any());
        verify(repository).delete(customer);
    }

    @Test
    void delete_Failed() {
        //given
        when(repository.findById("customerId")).thenReturn(Optional.empty());
        //when
        assertThrows(CustomerNotFoundException.class, () -> customerService.findByID("customerId"));
    }

    @Test
    void update() {
        //given
        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(customer, null);
        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest("newFirstName", "newLastName", "newUsername", "newPassword");
        CustomerDto customerDto1 = new CustomerDto("customerId", "newUsername", "newFirstName newLastName");

        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(mapper.fromModelToDto(customer)).thenReturn(customerDto1);
        //when
        CustomerDto result = customerService.update(updateCustomerRequest, upat);
        //then
        assertNotNull(result);
        assertEquals("newFirstName newLastName", result.fullName());
        assertEquals("newUsername", result.username());
        assertEquals("customerId", result.id());


        verify(passwordEncoder).encode("newPassword");
        verify(repository).save(any());

    }

    @Test
    void findAll() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        List<Customer> customerList = List.of(
                new Customer("customerId1", "username1", "password1", "firstName1", "lastName1", Role.USER),
                new Customer("customerId2", "username2", "password2", "firstName2", "lastName2", Role.USER),
                new Customer("customerId3", "username3", "password3", "firstName3", "lastName3", Role.USER)
        );

        List<CustomerDto> customerDtoList = List.of(
                new CustomerDto("customerId1", "username1", "firstName1 lastName1"),
                new CustomerDto("customerId2", "username2", "firstName2 lastName2"),
                new CustomerDto("customerId3", "username3", "firstName3 lastName3")
        );

        PageImpl<CustomerDto> dtoPage = new PageImpl<>(customerDtoList, pageable, customerDtoList.size());

        when(repository.findAll()).thenReturn(customerList);
        when(mapper.fromListOfEntityToDto(customerList)).thenReturn(customerDtoList);

        //when
        Page<CustomerDto> customerDtoPage = customerService.findAll(0, 5);

        //then
        assertEquals(3, customerDtoPage.getTotalElements());
        assertEquals(dtoPage.getTotalPages(), customerDtoPage.getTotalPages());

    }

    @Test
    void findById_Success() {
        //given
        String customerId = "customerId";
        when(repository.findById(customerId)).thenReturn(Optional.of(customer));
        when(mapper.fromModelToDto(customer)).thenReturn(customerDto);
        //when
        CustomerDto result = customerService.findByID("customerId");
        //then
        assertNotNull(result);
        assertEquals("customerId", result.id());
        assertEquals("username", result.username());
        assertEquals("firstName lastName", result.fullName());

        verify(repository).findById("customerId");
        verify(mapper).fromModelToDto(customer);

    }

    @Test
    void findByUsername_Success() {
        //given
        String username = "username";
        when(repository.findByUsername("username")).thenReturn(Optional.of(customer));
        when(mapper.fromModelToDto(customer)).thenReturn(customerDto);
        //when
        CustomerDto result = customerService.findByUsername(username);

        //then
        assertNotNull(result);
        assertEquals("customerId", result.id());
        assertEquals("username", result.username());
        assertEquals("firstName lastName", result.fullName());

        verify(repository).findByUsername("username");
        verify(mapper).fromModelToDto(customer);
    }

    @Test
    void findByUsername_Failed() {
        //given
        String username = "username";
        when(repository.findByUsername("username")).thenReturn(Optional.empty());
        //when
        assertThrows(CustomerNotFoundException.class, () -> customerService.findByUsername(username));
    }
}
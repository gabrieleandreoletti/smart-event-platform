package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.mapper.CustomerMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateCustomerRequest;
import com.sourcesense.smart_event_platform.persistance.CustomerRepository;
import com.sourcesense.smart_event_platform.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void delete() {
        //given
        Customer customer = new Customer("customerId", "username", "password", "firstName", "lastName", Role.USER);
        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        //when
        Boolean success = customerService.delete(customer.getId());

        //then
        verify(customerRepository).findById(any());
        verify(customerRepository).delete(customer);

        assertTrue(success);

    }

    @Test
    void update() {
        //given
        Customer customer = new Customer("customerId", "username", "password", "firstName", "lastName", Role.USER);
        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest("newCustomerFirstName", "newCustomerLastName", "newUsername", "newPassword");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(customer, null, null);
        CustomerDto expectedCustomerDto = new CustomerDto("customerId", "newUsername", "newCustomerFirstName newCustomerLastName");

        when(customerMapper.fromModelToDto(customer)).thenReturn(expectedCustomerDto);


        //when
        CustomerDto customerDto = customerService.update(updateCustomerRequest, usernamePasswordAuthenticationToken);

        //then
        assertNotNull(customerDto);
        assertEquals("customerId", customerDto.id());
        assertEquals("newUsername", customerDto.username());
        assertEquals("newCustomerFirstName newCustomerLastName", customerDto.fullName());
        assertEquals("newUsername", customer.getUsername());
        assertEquals("newCustomerFirstName", customer.getFirstName());
        assertEquals("newCustomerLastName", customer.getLastName());
        assertEquals("newPassword", customer.getPassword());

        verify(customerRepository).save(customer);

    }

    @Test
    void findAll() {
        //given
        Customer customer1 = new Customer("customerId1", "username1", "password1", "firstName1", "lastName1", Role.USER);
        Customer customer2 = new Customer("customerId2", "username2", "password2", "firstName2", "lastName2", Role.USER);
        CustomerDto customerDto1 = new CustomerDto("customerId1", "username1", "firstName1 lastName1");
        CustomerDto customerDto2 = new CustomerDto("customerId2", "username2", "firstName2 lastName2");

        List<Customer> customerList = List.of(customer1, customer2);
        List<CustomerDto> customerDtoList = List.of(customerDto1, customerDto2);

        when(customerRepository.findAll()).thenReturn(customerList);
        when(customerMapper.fromListOfEntityToDto(customerList)).thenReturn(customerDtoList);

        //when
        List<CustomerDto> resultCustomerList = customerService.findAll();

        //then
        assertNotNull(resultCustomerList);
        assertEquals(customerDtoList.get(0), resultCustomerList.get(0));
        assertEquals(customerDtoList.get(1), resultCustomerList.get(1));


    }

    @Test
    void findByID() {
        //given
        Customer customer = new Customer("customerId1", "username1", "password1", "firstName1", "lastName1", Role.USER);
        CustomerDto customerDto = new CustomerDto("customerId1", "username1", "firstName1 lastName1");

        when(customerRepository.findById("customerId1")).thenReturn(Optional.of(customer));
        when(customerMapper.fromModelToDto(customer)).thenReturn(customerDto);

        //when
        CustomerDto result = customerService.findByID("customerId1");

        assertEquals("username1", result.username());
        assertEquals("firstName1 lastName1", result.fullName());

        verify(customerRepository).findById("customerId1");
        verify(customerMapper).fromModelToDto(customer);
    }

    @Test
    void findByUsername() {
        //given
        Customer customer = new Customer("customerId1", "username1", "password1", "firstName1", "lastName1", Role.USER);
        CustomerDto customerDto = new CustomerDto("customerId1", "username1", "firstName1 lastName1");

        when(customerRepository.findByUsername("username1")).thenReturn(Optional.of(customer));
        when(customerMapper.fromModelToDto(customer)).thenReturn(customerDto);

        //when
        CustomerDto result = customerService.findByUsername("username1");

        assertEquals("username1", result.username());
        assertEquals("firstName1 lastName1", result.fullName());

        verify(customerRepository).findByUsername("username1");
        verify(customerMapper).fromModelToDto(customer);
    }
}
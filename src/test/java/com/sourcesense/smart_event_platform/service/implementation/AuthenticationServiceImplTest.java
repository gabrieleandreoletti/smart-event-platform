package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.mapper.CustomerMapper;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertCustomerRequest;
import com.sourcesense.smart_event_platform.model.dto.request.LoginCustomerRequest;
import com.sourcesense.smart_event_platform.persistance.CustomerRepository;
import com.sourcesense.smart_event_platform.security.Role;
import com.sourcesense.smart_event_platform.utility.JwtUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtility jwtUtility;
    @Mock
    private CustomerMapper customerMapper;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;


    @ParameterizedTest
    @CsvSource({
            "USER",
            "ORGANIZER",
            "ADMIN",
    })
    void registrationX(String roleName) {
        //given
        Role role = Role.valueOf(roleName);

        InsertCustomerRequest insertCustomerRequest = new InsertCustomerRequest("username", "password", "firstName", "lastName", role);
        Customer customer = new Customer("customerId", "username", "password", "firstName", "lastName", role);
        when(customerMapper.fromInsertRequestToModel(insertCustomerRequest)).thenReturn(customer);
        CustomerDto customerDto = new CustomerDto("customerId", "username", "firstName lastName");
        when(customerMapper.fromModelToDto(customer)).thenReturn(customerDto);
        when(passwordEncoder.encode(any())).thenReturn("encryptedPassword");
        //when
        CustomerDto result = authenticationService.registration(insertCustomerRequest);

        //then
        assertNotNull(result);
        assertEquals(role, customer.getRole());
        assertEquals("customerId", result.id());
        assertEquals("username", result.username());
        assertEquals("firstName lastName", result.fullName());

    }


    @Test
    void login() {
        //given
        LoginCustomerRequest loginCustomerRequest = new LoginCustomerRequest("username", "password");
        Customer customer = new Customer("customerId", "username", "encodedPassword", "firstName", "lastName", Role.USER);
        when(customerRepository.findByUsername(loginCustomerRequest.username())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(loginCustomerRequest.password(), customer.getPassword())).thenReturn(true);
        when(jwtUtility.generateToken(customer)).thenReturn("generatedAuthenticationToken");

        //when
        String token = authenticationService.login(loginCustomerRequest);

        //then
        assertNotNull(token);
        assertEquals("generatedAuthenticationToken", token);

        verify(customerRepository).findByUsername(anyString());
        verify(passwordEncoder).matches(anyString(), anyString());
    }
}
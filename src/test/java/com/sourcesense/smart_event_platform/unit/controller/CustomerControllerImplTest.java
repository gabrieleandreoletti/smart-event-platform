package com.sourcesense.smart_event_platform.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcesense.smart_event_platform.controller.implementation.CustomerControllerImpl;
import com.sourcesense.smart_event_platform.exception.CustomerNotFoundException;
import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateCustomerRequest;
import com.sourcesense.smart_event_platform.configuration.Role;
import com.sourcesense.smart_event_platform.configuration.TestConfiguration;
import com.sourcesense.smart_event_platform.service.definition.CustomerService;
import com.sourcesense.smart_event_platform.utility.JwtUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerControllerImpl.class)
@Import(TestConfiguration.class)
class CustomerControllerImplTest {

    private static final String BASE_PATH = "/api/v1/customers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtility jwtUtility;

    @MockitoBean
    private CustomerService service;

    Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("customerId", "username", "password", "firstName", "lastName", Role.USER);
    }

    @Test
    void delete_204() throws Exception {

        when(service.delete("1234")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/1234"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string("true"));

    }

    @Test
    void delete_400() throws Exception {
        when(service.delete(anyString())).thenThrow(CustomerNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/1234"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cause", is("com.sourcesense.smart_event_platform.exception.CustomerNotFoundException")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Bad Request")))
        ;

    }

    @Test
    void update_200() throws Exception {

        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest("newFirstName", "newLastName", "newUsername", null);

        CustomerDto updatedCustomer = new CustomerDto("customerId", "newUsername", "newFirstName newLastName");

        when(service.update(eq(updateCustomerRequest), any(UsernamePasswordAuthenticationToken.class))).thenReturn(updatedCustomer);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH)
                        .with(user(customer))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCustomerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("customerId")))
                .andExpect(jsonPath("$.username", is("newUsername")))
                .andExpect(jsonPath("$.fullName", is("newFirstName newLastName")));

    }


    @Test
    void update_400() throws Exception {

        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest("", ".2", "newUsername", null);


        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH)
                        .with(user(customer))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCustomerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.cause", is("com.sourcesense.smart_event_platform.exception.MethodArgumentNotValid")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Bad Request")));

    }

    @Test
    void findByID_200() throws Exception {
        CustomerDto expectedResponseBody = new CustomerDto("customerId", "username", "firstName lastName");

        when(service.findByID("customerId")).thenReturn(expectedResponseBody);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/customerId"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("customerId")))
                .andExpect(jsonPath("$.username", is("username")))
                .andExpect(jsonPath("$.fullName", is("firstName lastName")));

    }

    @Test
    void findById_400() throws Exception {
        when(service.findByID(anyString())).thenThrow(CustomerNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/1234"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cause", is("com.sourcesense.smart_event_platform.exception.CustomerNotFoundException")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Bad Request")))
        ;

    }

    @Test
    void findByUsername() throws Exception {
        CustomerDto expectedResponseBody = new CustomerDto("customerId", "username", "firstName lastName");

        when(service.findByUsername("username")).thenReturn(expectedResponseBody);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/username/username"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("customerId")))
                .andExpect(jsonPath("$.username", is("username")))
                .andExpect(jsonPath("$.fullName", is("firstName lastName")));

    }

    @Test
    void findByUsername_400() throws Exception {
        when(service.findByUsername(anyString())).thenThrow(CustomerNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/username/username"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cause", is("com.sourcesense.smart_event_platform.exception.CustomerNotFoundException")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Bad Request")))
        ;

    }

    @Test
    void findAll_200() throws Exception {
        List<CustomerDto> expectedCustomerList = List.of(
                new CustomerDto("id1", "user1", "name1"),
                new CustomerDto("id2", "user2", "name2")
        );

        Pageable pageable = PageRequest.of(0, 2);
        Page<CustomerDto> page = new PageImpl<>(expectedCustomerList, pageable, 2);

        when(service.findAll(0, 2)).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                        .param("page", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is("id1")))
                .andExpect(jsonPath("$.content[0].username", is("user1")))
                .andExpect(jsonPath("$.content[0].fullName", is("name1")))
                .andExpect(jsonPath("$.content[1].username", is("user2")))
                .andExpect(jsonPath("$.content[1].fullName", is("name2")))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.pageable.pageSize", is(2)))
                .andExpect(jsonPath("$.totalElements", is(2))
                );

    }
}



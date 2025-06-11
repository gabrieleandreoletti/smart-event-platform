package com.sourcesense.smart_event_platform.mapper;

import com.sourcesense.smart_event_platform.model.Customer;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertCustomerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer fromInsertRequestToModel(InsertCustomerRequest request);

    @Mapping(target = "fullName", source = ".", qualifiedByName = "combineFullName")
    CustomerDto fromModelToDto(Customer model);

    @Mapping(target = "fullName", source = ".", qualifiedByName = "combineFullName")
    List<CustomerDto> fromListOfEntityToDto(List<Customer> customerList);


    @Named("combineFullName")
    static String combineFullName(Customer customer) {
        return customer.getFirstName() + " " + customer.getLastName();
    }

}

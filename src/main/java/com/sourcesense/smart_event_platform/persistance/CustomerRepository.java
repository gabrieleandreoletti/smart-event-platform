package com.sourcesense.smart_event_platform.persistance;

import com.sourcesense.smart_event_platform.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByUsername(String username);

    Boolean existsByUsernameAndId(String username, String customerID);
}

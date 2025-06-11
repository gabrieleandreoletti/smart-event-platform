package com.sourcesense.smart_event_platform.service.implementation;

import com.sourcesense.smart_event_platform.exception.CustomerNotFoundException;
import com.sourcesense.smart_event_platform.persistance.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final CustomerRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username).orElseThrow(() -> new CustomerNotFoundException("There is no customer with username " + username));
    }

}

package com.sourcesense.smart_event_platform.controller.implementation;

import com.sourcesense.smart_event_platform.controller.definition.AuthController;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertCustomerRequest;
import com.sourcesense.smart_event_platform.model.dto.request.LoginCustomerRequest;
import com.sourcesense.smart_event_platform.service.definition.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthControllerImpl implements AuthController {

    private final AuthenticationService authenticationService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation
    public CustomerDto registration(@RequestBody @Valid InsertCustomerRequest insertRequest) {
        return authenticationService.registration(insertRequest);
    }


    @Override
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginCustomerRequest loginRequest) {
        String token = authenticationService.login(loginRequest);
        return ResponseEntity.ok().header("Authorization", token).body(token);
    }
}

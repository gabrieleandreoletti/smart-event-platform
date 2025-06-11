package com.sourcesense.smart_event_platform.controller.implementation;

import com.sourcesense.smart_event_platform.controller.definition.CustomerController;
import com.sourcesense.smart_event_platform.model.dto.CustomerDto;
import com.sourcesense.smart_event_platform.model.dto.request.UpdateCustomerRequest;
import com.sourcesense.smart_event_platform.service.definition.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/customers")
@Tag(name = "CustomerApi", description = "Operazioni relative agli utenti")
public class CustomerControllerImpl implements CustomerController {

    private final CustomerService service;

    @Override
    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminazione utente",
            description = "Eliminazione di un utente mediante un ID , questa operazione può essere effettuata solo da admin",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cliente trovato ed eliminato"),
                    @ApiResponse(responseCode = "400", description = "Id non trovato nel database"),
                    @ApiResponse(responseCode = "403", description = "Cliente che effettua l'operazione non è autenticato o autorizzato"
                    )})
    public Boolean delete(@PathVariable String customerId) {
        return service.delete(customerId);
    }

    @Override
    @PutMapping
    @Operation(
            summary = "Modifica utente",
            description = "Aggiorna i dati di un utente autenticato",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "I nuovi dati dell'utente",
                    content = @Content(schema = @Schema(implementation = UpdateCustomerRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cliente trovato ed eliminato"),
                    @ApiResponse(responseCode = "400", description = "Dati inseriti nel body non validi"),
                    @ApiResponse(responseCode = "403", description = "Cliente che effettua l'operazione non è autenticato o autorizzato"
                    )})
    public CustomerDto update(@RequestBody @Valid UpdateCustomerRequest updateRequest, UsernamePasswordAuthenticationToken token) {
        return service.update(updateRequest, token);
    }

    @Override
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Ricerca di un utente per ID",
            description = "Ricerca i dati di un utente mediante Id e ne restituisce il risultato. Operazione effettuabile solo da admin.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente trovato e restituito"),
                    @ApiResponse(responseCode = "400", description = "Id non trovato nel database"),
                    @ApiResponse(responseCode = "403", description = "Cliente che effettua l'operazione non è autenticato o autorizzato")}
    )
    public CustomerDto findByID(@PathVariable String customerId) {
        return service.findByID(customerId);
    }

    @Override
    @GetMapping("/username/{username}")
    @Operation(
            summary = "Ricerca di un utente per Username",
            description = "Ricerca i dati di un ute9nte mediante username e ne restituisce il risultato.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente trovato e restituito"),
                    @ApiResponse(responseCode = "400", description = "Username non trovato nel database"),
            }
    )
    public CustomerDto findByUsername(@PathVariable String username) {
        return service.findByUsername(username);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Lista di utenti paginata",
            description = "Restituisce la lista di utenti paginata in base ai parametri.",
            parameters = {
                    @Parameter(name = "page", description = "il numero della pagina che si vuole restituire"),
                    @Parameter(name = "size", description = "il numero di oggetti restituiti nella pagina")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Restituzione dei clienti"),
                    @ApiResponse(responseCode = "403", description = "Utente non autorizzato"),
            }
    )
    public Page<CustomerDto> findAll(@RequestParam int page, @RequestParam int size) {
        return service.findAll(page, size);
    }


}

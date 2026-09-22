package com.civicshield.safestop.api;

import com.civicshield.safestop.domain.SafeStopRequest;
import com.civicshield.safestop.service.SafeStopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/safestop")
public class SafeStopController {

    private final SafeStopService service;

    public SafeStopController(SafeStopService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SafeStopResponse create(
            @Valid @RequestBody CreateSafeStopRequest request
    ) {
        return SafeStopResponse.from(
                service.create(
                        request.incidentId(),
                        request.vehicleId(),
                        request.requestedBy(),
                        request.reason()
                )
        );
    }

    @GetMapping
    public List<SafeStopResponse> list(
            @RequestParam(required = false) String incidentId
    ) {
        return service.list(incidentId)
                .stream()
                .map(SafeStopResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public SafeStopResponse get(
            @PathVariable UUID id
    ) {
        return SafeStopResponse.from(
                service.get(id)
        );
    }

    @PostMapping("/{id}/authorize")
    public SafeStopResponse authorize(
            @PathVariable UUID id,
            @Valid @RequestBody AuthorizationRequest request
    ) {
        try {
            return SafeStopResponse.from(
                    service.authorize(
                            id,
                            request.authorizedBy()
                    )
            );
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage()
            );
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    ex.getMessage()
            );
        }
    }

    @PostMapping("/{id}/deny")
    public SafeStopResponse deny(
            @PathVariable UUID id,
            @Valid @RequestBody AuthorizationRequest request
    ) {
        try {
            return SafeStopResponse.from(
                    service.deny(
                            id,
                            request.authorizedBy()
                    )
            );
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage()
            );
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    ex.getMessage()
            );
        }
    }

    @PostMapping("/{id}/execute")
    public SafeStopResponse execute(
            @PathVariable UUID id
    ) {
        try {
            return SafeStopResponse.from(
                    service.execute(id)
            );
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage()
            );
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    ex.getMessage()
            );
        }
    }
}

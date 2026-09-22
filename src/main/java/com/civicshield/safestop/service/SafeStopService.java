package com.civicshield.safestop.service;

import com.civicshield.safestop.adapter.VehicleInterventionAdapter;
import com.civicshield.safestop.domain.SafeStopRepository;
import com.civicshield.safestop.domain.SafeStopRequest;
import com.civicshield.safestop.platform.PlatformAuditClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SafeStopService {

    private final SafeStopRepository repository;
    private final List<VehicleInterventionAdapter> adapters;
    private final PlatformAuditClient audit;

    public SafeStopService(
            SafeStopRepository repository,
            List<VehicleInterventionAdapter> adapters,
            PlatformAuditClient audit
    ) {
        this.repository = repository;
        this.adapters = adapters;
        this.audit = audit;
    }

    @Transactional
    public SafeStopRequest create(
            String incidentId,
            String vehicleId,
            String requestedBy,
            String reason
    ) {
        SafeStopRequest request = repository.save(
                new SafeStopRequest(
                        incidentId,
                        vehicleId,
                        requestedBy,
                        reason
                )
        );

        audit.event(
                incidentId,
                "SAFESTOP_REQUESTED",
                requestedBy,
                "SafeStop request " + request.getId()
                        + " created for synthetic vehicle "
                        + vehicleId
        );

        return request;
    }

    @Transactional
    public SafeStopRequest authorize(
            UUID id,
            String authorizedBy
    ) {
        SafeStopRequest request = get(id);

        request.authorize(authorizedBy);

        audit.event(
                request.getIncidentId(),
                "SAFESTOP_AUTHORIZED",
                authorizedBy,
                "Human authorization recorded for SafeStop request "
                        + request.getId()
        );

        return repository.save(request);
    }

    @Transactional
    public SafeStopRequest deny(
            UUID id,
            String actor
    ) {
        SafeStopRequest request = get(id);

        request.deny(actor);

        audit.event(
                request.getIncidentId(),
                "SAFESTOP_DENIED",
                actor,
                "SafeStop request " + request.getId() + " denied."
        );

        return repository.save(request);
    }

    @Transactional
    public SafeStopRequest execute(UUID id) {
        SafeStopRequest request = get(id);

        VehicleInterventionAdapter adapter =
                adapters.stream()
                        .filter(a ->
                                a.supports(request.getVehicleId())
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No approved adapter supports vehicle "
                                                + request.getVehicleId()
                                )
                        );

        request.startExecution();
        repository.save(request);

        audit.event(
                request.getIncidentId(),
                "SAFESTOP_EXECUTION_STARTED",
                request.getAuthorizedBy(),
                "Executing via adapter " + adapter.name()
        );

        try {
            adapter.requestControlledSlowdown(
                    request.getVehicleId()
            );

            request.complete();

            audit.event(
                    request.getIncidentId(),
                    "SAFESTOP_EXECUTION_COMPLETED",
                    request.getAuthorizedBy(),
                    "Simulator accepted the constrained slowdown request."
            );

            return repository.save(request);

        } catch (Exception ex) {
            request.fail(ex.getMessage());

            audit.event(
                    request.getIncidentId(),
                    "SAFESTOP_EXECUTION_FAILED",
                    request.getAuthorizedBy(),
                    ex.getMessage()
            );

            return repository.save(request);
        }
    }

    @Transactional(readOnly = true)
    public SafeStopRequest get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "SafeStop request not found: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<SafeStopRequest> list(String incidentId) {
        if (incidentId == null || incidentId.isBlank()) {
            return repository.findAll();
        }

        return repository
                .findByIncidentIdOrderByRequestedAtDesc(incidentId);
    }
}

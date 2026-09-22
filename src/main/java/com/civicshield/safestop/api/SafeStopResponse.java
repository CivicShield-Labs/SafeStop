package com.civicshield.safestop.api;

import com.civicshield.safestop.domain.SafeStopRequest;
import com.civicshield.safestop.domain.SafeStopStatus;

import java.time.Instant;
import java.util.UUID;

public record SafeStopResponse(
        UUID id,
        String incidentId,
        String vehicleId,
        String requestedBy,
        String reason,
        SafeStopStatus status,
        String authorizedBy,
        Instant requestedAt,
        Instant authorizedAt,
        Instant completedAt,
        String failureReason
) {
    public static SafeStopResponse from(SafeStopRequest request) {
        return new SafeStopResponse(
                request.getId(),
                request.getIncidentId(),
                request.getVehicleId(),
                request.getRequestedBy(),
                request.getReason(),
                request.getStatus(),
                request.getAuthorizedBy(),
                request.getRequestedAt(),
                request.getAuthorizedAt(),
                request.getCompletedAt(),
                request.getFailureReason()
        );
    }
}

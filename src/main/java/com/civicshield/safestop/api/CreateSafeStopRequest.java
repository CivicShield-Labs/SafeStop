package com.civicshield.safestop.api;

import jakarta.validation.constraints.NotBlank;

public record CreateSafeStopRequest(
        @NotBlank String incidentId,
        @NotBlank String vehicleId,
        @NotBlank String requestedBy,
        @NotBlank String reason
) {
}

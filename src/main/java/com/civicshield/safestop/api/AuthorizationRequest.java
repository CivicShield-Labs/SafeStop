package com.civicshield.safestop.api;

import jakarta.validation.constraints.NotBlank;

public record AuthorizationRequest(
        @NotBlank String authorizedBy
) {
}

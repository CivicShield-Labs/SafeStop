package com.civicshield.safestop.platform;

public record AuditEventRequest(
        String action,
        String actor,
        String details
) {
}

package com.civicshield.safestop.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "safestop_requests")
public class SafeStopRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "incident_id", nullable = false)
    private String incidentId;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Column(name = "requested_by", nullable = false)
    private String requestedBy;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SafeStopStatus status;

    @Column(name = "authorized_by")
    private String authorizedBy;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "authorized_at")
    private Instant authorizedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "failure_reason")
    private String failureReason;

    protected SafeStopRequest() {
    }

    public SafeStopRequest(
            String incidentId,
            String vehicleId,
            String requestedBy,
            String reason
    ) {
        this.incidentId = incidentId;
        this.vehicleId = vehicleId;
        this.requestedBy = requestedBy;
        this.reason = reason;
        this.status = SafeStopStatus.REQUESTED;
        this.requestedAt = Instant.now();
    }

    public void authorize(String actor) {
        if (status != SafeStopStatus.REQUESTED) {
            throw new IllegalStateException("Only REQUESTED interventions can be authorized.");
        }

        if (actor != null && actor.equalsIgnoreCase(requestedBy)) {
            throw new IllegalStateException(
                    "SafeStop authorization must be performed by someone other than the requester."
            );
        }

        status = SafeStopStatus.AUTHORIZED;
        authorizedBy = actor;
        authorizedAt = Instant.now();
    }

    public void deny(String actor) {
        if (status != SafeStopStatus.REQUESTED) {
            throw new IllegalStateException("Only REQUESTED interventions can be denied.");
        }

        status = SafeStopStatus.DENIED;
        authorizedBy = actor;
        authorizedAt = Instant.now();
    }

    public void startExecution() {
        if (status != SafeStopStatus.AUTHORIZED) {
            throw new IllegalStateException("SafeStop must be human-authorized before execution.");
        }

        status = SafeStopStatus.EXECUTING;
    }

    public void complete() {
        if (status != SafeStopStatus.EXECUTING) {
            throw new IllegalStateException("SafeStop is not executing.");
        }

        status = SafeStopStatus.COMPLETED;
        completedAt = Instant.now();
    }

    public void fail(String failureReason) {
        status = SafeStopStatus.FAILED;
        this.failureReason = failureReason;
        completedAt = Instant.now();
    }

    public void cancel() {
        if (status == SafeStopStatus.COMPLETED || status == SafeStopStatus.FAILED) {
            throw new IllegalStateException("Completed SafeStop requests cannot be cancelled.");
        }

        status = SafeStopStatus.CANCELLED;
        completedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getReason() {
        return reason;
    }

    public SafeStopStatus getStatus() {
        return status;
    }

    public String getAuthorizedBy() {
        return authorizedBy;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Instant getAuthorizedAt() {
        return authorizedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }
}

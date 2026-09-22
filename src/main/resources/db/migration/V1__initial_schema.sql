CREATE TABLE safestop_requests (
    id UUID PRIMARY KEY,
    incident_id VARCHAR(255) NOT NULL,
    vehicle_id VARCHAR(255) NOT NULL,
    requested_by VARCHAR(255) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status VARCHAR(64) NOT NULL,
    authorized_by VARCHAR(255),
    requested_at TIMESTAMP WITH TIME ZONE NOT NULL,
    authorized_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    failure_reason VARCHAR(2000)
);

CREATE INDEX idx_safestop_incident
    ON safestop_requests(incident_id);

CREATE INDEX idx_safestop_vehicle
    ON safestop_requests(vehicle_id);

CREATE INDEX idx_safestop_status
    ON safestop_requests(status);

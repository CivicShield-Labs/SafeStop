package com.civicshield.safestop.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SafeStopRequestTest {

    @Test
    void requiresAuthorizationBeforeExecution() {
        SafeStopRequest request =
                new SafeStopRequest(
                        "INCIDENT-1",
                        "SIM-004",
                        "officer-demo",
                        "Synthetic fleeing vehicle"
                );

        assertEquals(
                SafeStopStatus.REQUESTED,
                request.getStatus()
        );

        assertThrows(
                IllegalStateException.class,
                request::startExecution
        );
    }

    @Test
    void supportsHumanAuthorizationLifecycle() {
        SafeStopRequest request =
                new SafeStopRequest(
                        "INCIDENT-1",
                        "SIM-004",
                        "officer-demo",
                        "Synthetic fleeing vehicle"
                );

        request.authorize("dispatcher-demo");

        assertEquals(
                SafeStopStatus.AUTHORIZED,
                request.getStatus()
        );

        assertEquals(
                "dispatcher-demo",
                request.getAuthorizedBy()
        );

        request.startExecution();

        assertEquals(
                SafeStopStatus.EXECUTING,
                request.getStatus()
        );

        request.complete();

        assertEquals(
                SafeStopStatus.COMPLETED,
                request.getStatus()
        );
    }

    @Test
    void rejectsSelfAuthorizationByTheSameRequester() {
        SafeStopRequest request =
                new SafeStopRequest(
                        "INCIDENT-1",
                        "SIM-004",
                        "officer-demo",
                        "Synthetic fleeing vehicle"
                );

        assertThrows(
                IllegalStateException.class,
                () -> request.authorize("officer-demo")
        );

        assertThrows(
                IllegalStateException.class,
                () -> request.authorize("Officer-Demo")
        );

        assertEquals(
                SafeStopStatus.REQUESTED,
                request.getStatus()
        );
    }
}

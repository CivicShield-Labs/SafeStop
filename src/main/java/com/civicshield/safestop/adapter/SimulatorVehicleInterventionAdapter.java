package com.civicshield.safestop.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SimulatorVehicleInterventionAdapter
        implements VehicleInterventionAdapter {

    private final RestClient simulatorClient;

    public SimulatorVehicleInterventionAdapter(
            @Value("${civicshield.simulator.base-url}") String simulatorBaseUrl
    ) {
        this.simulatorClient = RestClient.builder()
                .baseUrl(simulatorBaseUrl)
                .build();
    }

    @Override
    public String name() {
        return "CIVICSHIELD_SIMULATOR";
    }

    @Override
    public boolean supports(String vehicleId) {
        return vehicleId != null && vehicleId.startsWith("SIM-");
    }

    @Override
    public void requestControlledSlowdown(String vehicleId) {
        if (!supports(vehicleId)) {
            throw new IllegalArgumentException(
                    "Simulator adapter supports synthetic SIM-* vehicles only."
            );
        }

        simulatorClient.post()
                .uri(
                        "/api/simulator/vehicles/{vehicleId}/slowdown",
                        vehicleId
                )
                .retrieve()
                .toBodilessEntity();
    }
}

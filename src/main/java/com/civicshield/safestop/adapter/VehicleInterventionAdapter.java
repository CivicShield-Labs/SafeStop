package com.civicshield.safestop.adapter;

public interface VehicleInterventionAdapter {

    String name();

    boolean supports(String vehicleId);

    void requestControlledSlowdown(String vehicleId);
}

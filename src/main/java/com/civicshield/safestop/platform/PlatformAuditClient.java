package com.civicshield.safestop.platform;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PlatformAuditClient {

    private final RestClient client;

    public PlatformAuditClient(
            @Value("${civicshield.platform.base-url}") String baseUrl,
            @Value("${civicshield.platform.username}") String username,
            @Value("${civicshield.platform.password}") String password
    ) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers ->
                        headers.setBasicAuth(username, password)
                )
                .build();
    }

    public void event(
            String incidentId,
            String action,
            String actor,
            String details
    ) {
        try {
            client.post()
                    .uri(
                            "/api/incidents/{incidentId}/events",
                            incidentId
                    )
                    .body(
                            new AuditEventRequest(
                                    action,
                                    actor,
                                    details
                            )
                    )
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            System.err.println(
                    "Unable to publish CivicShield audit event: "
                            + ex.getMessage()
            );
        }
    }
}

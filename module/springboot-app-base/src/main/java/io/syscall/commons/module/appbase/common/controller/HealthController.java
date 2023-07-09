package io.syscall.commons.module.appbase.common.controller;

import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private static final ResponseEntity<String> UP_RESPONSE = ResponseEntity.ok("UP");
    private static final ResponseEntity<String> DOWN_RESPONSE =
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("DOWN");
    private final ApplicationAvailability availability;

    public HealthController(ApplicationAvailability availability) {
        this.availability = availability;
    }

    @GetMapping({"/internal/healthz", "/internal/healthz/ready"})
    public ResponseEntity<String> readiness() {
        var state = availability.getReadinessState();
        if (state == ReadinessState.ACCEPTING_TRAFFIC) {
            return UP_RESPONSE;
        }
        return DOWN_RESPONSE;
    }

    @GetMapping("/internal/healthz/live")
    public ResponseEntity<String> liveness() {
        var state = availability.getLivenessState();
        if (state == LivenessState.CORRECT) {
            return UP_RESPONSE;
        }
        return DOWN_RESPONSE;
    }

    @GetMapping("/internal/healthz/startup")
    public ResponseEntity<String> startup() {
        return liveness();
    }
}

package org.mlesyk.staticdata.controller;

import org.mlesyk.staticdata.util.Healthcheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthcheckController {

    private final Healthcheck healthcheck;

    @Autowired
    public HealthcheckController(Healthcheck healthcheck) {
        this.healthcheck = healthcheck;
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<String> isHealthy() {
        if(healthcheck.isServiceHealthy()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Healthy");
        } else {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Not ready");
        }
    }
}
package org.mlesyk.staticdata;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.mlesyk.staticdata.service.DataInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class AppCommandLineRunner {
    private final DataInitializationService dataInitializationService;

    @Autowired
    public AppCommandLineRunner(DataInitializationService dataInitializationService) {
        this.dataInitializationService = dataInitializationService;
    }

    @PostConstruct
    void init() {
        dataInitializationService.initializeData();
    }
}
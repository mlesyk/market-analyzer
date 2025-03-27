package org.mlesyk.orders.analyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public ReentrantReadWriteLock readWriteLock() {
        return new ReentrantReadWriteLock();
    }
}

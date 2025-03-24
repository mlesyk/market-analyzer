package org.mlesyk.marketapi.config;

import org.mlesyk.marketapi.client.RetryableRequestHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.errorHandler(new RetryableRequestHandler.RestTemplateResponseErrorHandler()).build();
    }
}

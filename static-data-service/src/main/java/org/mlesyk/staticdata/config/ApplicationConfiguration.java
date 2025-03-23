package org.mlesyk.staticdata.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ApplicationConfiguration {

    @Bean
    @Primary
    public ObjectMapper jsonObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ObjectMapper yamlObjectMapper() {
        return new ObjectMapper(yamlFactory());
    }

    @Bean
    public YAMLFactory yamlFactory() {
        return new YAMLFactory();
    }

}

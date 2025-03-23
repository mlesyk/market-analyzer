package org.mlesyk.staticdata.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.yaml.snakeyaml.LoaderOptions;

@Configuration
public class ApplicationConfiguration {

    @Value("${app.snakeyaml.maxYamlCodePoints}")
    private Integer maxYamlCodePoints;

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
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setCodePointLimit(maxYamlCodePoints); // Set your desired code point limit here
        return YAMLFactory.builder().loaderOptions(loaderOptions).build();
    }

}

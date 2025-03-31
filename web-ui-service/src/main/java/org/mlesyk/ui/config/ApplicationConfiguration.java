package org.mlesyk.ui.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mlesyk.ui.model.WebsiteData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Value("${app.website_name_url}")
    String websiteNameUrl;

    @Value("${app.website_name_text}")
    String websiteNameText;

    @Value("${app.years_active}")
    String yearsActive;

    @Bean
    public ObjectMapper jsonObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public WebsiteData websiteData() {
        return new WebsiteData(websiteNameUrl, websiteNameText, yearsActive);
    }
}

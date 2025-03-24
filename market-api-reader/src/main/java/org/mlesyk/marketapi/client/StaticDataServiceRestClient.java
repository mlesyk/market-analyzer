package org.mlesyk.marketapi.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class StaticDataServiceRestClient {

    @Value("${app.client.static_data.url}")
    private String staticDataURL;

    @Value("${app.client.static_data.types_path}")
    private String typeIdListPath;

    private final RestTemplate restTemplate;

    @Autowired
    public StaticDataServiceRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Set<Integer> getAllItemIds() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(staticDataURL + typeIdListPath);
        Integer[] itemIds = restTemplate.getForObject(builder.build().toUriString(), Integer[].class);
        return itemIds != null && itemIds.length > 0 ? new HashSet<>(Arrays.asList(itemIds)) : Collections.emptySet();
    }

}

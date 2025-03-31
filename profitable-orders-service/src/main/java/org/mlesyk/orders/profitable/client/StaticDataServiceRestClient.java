package org.mlesyk.orders.profitable.client;

import org.mlesyk.orders.profitable.model.rest.MarketTypeNameToIdDTO;
import org.mlesyk.orders.profitable.model.rest.MarketTypeRestDTO;
import org.mlesyk.orders.profitable.model.rest.UniverseSystemRestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class StaticDataServiceRestClient {

    private final RestTemplate restTemplate;

    @Autowired
    public StaticDataServiceRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${app.client.static_data.url}")
    private String staticDataURL;

    @Value("${app.client.static_data.types_path}")
    private String itemsListPath;

    @Value("${app.client.static_data.systems_path}")
    private String systemsListPath;

    @Value("${app.client.static_data.type_name_to_id_path}")
    private String typeNameToIdListPath;

    public Map<Integer, MarketTypeRestDTO> getAllItems() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(staticDataURL + itemsListPath);
        Map<Integer, MarketTypeRestDTO> items = (Map<Integer, MarketTypeRestDTO>)restTemplate.getForObject(builder.build().toUriString(), Map.class);
        return items != null && !items.isEmpty() ? items : Collections.emptyMap();
    }

    public List<MarketTypeNameToIdDTO> getMarketTypeNameToIdList() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(staticDataURL + typeNameToIdListPath);
        MarketTypeNameToIdDTO[] result = restTemplate.getForObject(builder.build().toUriString(), MarketTypeNameToIdDTO[].class);
        return result != null && result.length > 0 ? Arrays.asList(result) : Collections.emptyList();
    }

    public MarketTypeRestDTO getItemById(Integer id) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(staticDataURL + itemsListPath + "/" + id);
        return restTemplate.getForObject(builder.build().toUriString(), MarketTypeRestDTO.class);
    }

    public String getItemStringById(Integer id) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(staticDataURL + itemsListPath + "/" + id);
        return restTemplate.getForObject(builder.build().toUriString(), String.class);
    }

    public UniverseSystemRestDTO getUniverseSystemById(Integer systemID) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(staticDataURL + systemsListPath + "/" + systemID);
        return restTemplate.getForObject(builder.build().toUriString(), UniverseSystemRestDTO.class);
    }

    public List<UniverseSystemRestDTO> getUniverseSystemsById(List<Integer> systemIDs) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(staticDataURL + systemsListPath);
        builder.queryParam("systemIDs", systemIDs);
        UniverseSystemRestDTO[] systems = restTemplate.getForObject(builder.build().toUriString(), UniverseSystemRestDTO[].class);
        return systems != null && systems.length > 0 ? Arrays.asList(systems) : Collections.emptyList();
    }
}
package org.mlesyk.ui.client;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.ui.model.ProfitableOrdersViewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@Slf4j
public class ProfitableOrdersServiceClient {

    private final RestTemplate restTemplate;

    @Value("${app.client.orders_data.url}")
    private String ordersHandlerURL;

    @Value("${app.client.orders_data.orders_path}")
    private String ordersPath;

    @Autowired
    public ProfitableOrdersServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProfitableOrdersViewDTO[] findProfitableOrders(Map<String, Object> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(ordersHandlerURL + ordersPath);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            builder.queryParam(param.getKey(), param.getValue());
        }
        try {
            return restTemplate.getForObject(builder.build().toUriString(), ProfitableOrdersViewDTO[].class);
        } catch (RestClientException e) {
            log.error("Orders Data is not available: ", e);
        }
        return new ProfitableOrdersViewDTO[0];
    }
}

package org.mlesyk.orders.profitable.client;

import org.mlesyk.orders.profitable.model.rest.RegionOrderAvgPriceCalculatedDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class OrderStorageServiceClient {

    private final RestTemplate restTemplate;

    @Autowired
    public OrderStorageServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${app.client.storage_service.url}")
    private String storageServiceURL;

    @Value("${app.client.storage_service.sell_orders_avg_price_path}")
    private String sellOrdersAvgPricesPath;

    @Value("${app.client.storage_service.buy_orders_avg_price_path}")
    private String buyOrdersAvgPricesPath;

    public List<RegionOrderAvgPriceCalculatedDTO> getSellOrdersAvgPrices() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(storageServiceURL + sellOrdersAvgPricesPath);
        RegionOrderAvgPriceCalculatedDTO[] sellOrdersAvgPrices = restTemplate.getForObject(builder.build().toUriString(), RegionOrderAvgPriceCalculatedDTO[].class);

        return sellOrdersAvgPrices != null && sellOrdersAvgPrices.length > 0 ? Arrays.asList(sellOrdersAvgPrices) : Collections.emptyList();
    }

    public List<RegionOrderAvgPriceCalculatedDTO> getBuyOrdersAvgPrices() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(storageServiceURL + buyOrdersAvgPricesPath);
        RegionOrderAvgPriceCalculatedDTO[] buyOrdersAvgPrices = restTemplate.getForObject(builder.build().toUriString(), RegionOrderAvgPriceCalculatedDTO[].class);

        return buyOrdersAvgPrices != null && buyOrdersAvgPrices.length > 0 ? Arrays.asList(buyOrdersAvgPrices) : Collections.emptyList();
    }
}

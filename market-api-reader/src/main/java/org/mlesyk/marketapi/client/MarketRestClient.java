package org.mlesyk.marketapi.client;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.marketapi.model.MarketOrder;
import org.mlesyk.marketapi.model.MarketOrderStatistics;
import org.mlesyk.marketapi.model.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Component
public class MarketRestClient {

    @Value("${app.market.api.url}")
    private String apiURL;

    @Value("${app.market.api.types_path}")
    private String typeIdListByRegionPath;

    @Value("${app.market.api.orders_path}")
    private String orderListByRegionPath;

    @Value("${app.market.api.statistics_path}")
    private String ordersStatisticsByRegionPath;

    @Value("${app.market.api.regions_path}")
    private String universeRegionsPath;

    @Value("${app.market.api.regions_info_path}")
    private String universeRegionInfoPath;

    @Value("${app.market.api.route_path}")
    private String routeCalculatorPath;

    private final RestTemplate restTemplate;
    private final RetryableRequestHandler retryableRequestHandler;

    @Autowired
    public MarketRestClient(RestTemplate restTemplate, RetryableRequestHandler retryableRequestHandler) {
        this.restTemplate = restTemplate;
        this.retryableRequestHandler = retryableRequestHandler;
    }

    public Integer[] getTypeIdListByRegion(Integer regionId, Integer page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiURL + typeIdListByRegionPath);
        if (page != null && page > 1) {
            builder.queryParam("page", page);
        } else {
            builder.queryParam("page", 1);
        }
        String uri = builder.build().toUriString();
        return restTemplate.getForObject(uri, Integer[].class, regionId);
    }

    public List<MarketOrder> getRegionOrderInfoList(Map<String, Object> params) {
        Integer regionId = (Integer) params.get("region_id_path_param");
        MarketOrder[] marketRegionOrders = retryableRequestHandler.getWithRetry(applyQueryParameters(apiURL + orderListByRegionPath, params), MarketOrder[].class, params);
        if(marketRegionOrders != null && marketRegionOrders.length > 0) {
            for (MarketOrder order : marketRegionOrders) {
                order.setRegionId(regionId);
                order.setOrderValue(order.getPrice() * order.getVolumeRemain());
            }
            return Arrays.asList(marketRegionOrders);
        }
        return Collections.emptyList();
    }

    public List<MarketOrderStatistics> getMarketRegionOrderStatisticsInfoList(Integer regionId, Integer typeId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiURL + ordersStatisticsByRegionPath);
        if (typeId != null) {
            builder.queryParam("type_id", typeId);
        } else {
            throw new NullPointerException("type_id should not be null");
        }
        MarketOrderStatistics[] marketRegionOrdersStatistics = restTemplate.getForObject(builder.build().toUriString(), MarketOrderStatistics[].class, regionId);
        return marketRegionOrdersStatistics != null ? Arrays.asList(marketRegionOrdersStatistics) : Collections.emptyList();
    }

    public Integer[] getUniverseRegionIds() {
        log.info("getUniverseRegionIds");
        return restTemplate.getForObject(apiURL + universeRegionsPath, Integer[].class);
    }

    public Region getUniverseRegionInfo(Integer regionId) {
        return restTemplate.getForObject(apiURL + universeRegionInfoPath, Region.class, regionId);
    }

    public List<Integer> calculateRouteBetweenSystems(Map<String, Object> params) {
        Integer[] routeBetweenSystems = retryableRequestHandler.getWithRetry(applyQueryParameters(apiURL + routeCalculatorPath, params), Integer[].class, params);
        return routeBetweenSystems != null ? Arrays.asList(routeBetweenSystems) : Collections.emptyList();
    }

    private String applyQueryParameters(String url, Map<String, Object> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getKey().contains("path_param")) {
                builder.queryParam(entry.getKey(), "{" + entry.getKey() + "}");
            }
        }
        return builder.build().toUriString();
    }
}

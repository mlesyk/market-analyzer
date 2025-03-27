package org.mlesyk.orders.profitable.client;

import org.mlesyk.orders.profitable.model.rest.RegionOrderStatisticsRestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@Component
public class MarketApiReaderClient {

    private final RestTemplate restTemplate;

    @Autowired
    public MarketApiReaderClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${app.client.market_api_reader.url}")
    private String esiReaderURL;

    @Value("${app.client.market_api_reader.route_path}")
    private String routePath;

    @Value("${app.client.market_api_reader.statistics_path}")
    private String statisticsPathWithPlaceHolder;

    public List<Integer> getShortestRoute(Integer from, Integer to, List<Integer> avoidRouteRegionIds) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(esiReaderURL + routePath);
        builder.queryParam("from", from);
        builder.queryParam("to", to);
        builder.queryParam("routeType", "shortest");
        if(!avoidRouteRegionIds.isEmpty()) {
            builder.queryParam("avoidRegionIds", avoidRouteRegionIds);
        }
        List<Integer> route = (List<Integer>)restTemplate.getForObject(builder.build().toUriString(), List.class);
        return route != null && !route.isEmpty() ? route : Collections.emptyList();
    }


    public List<Integer> getSecureRoute(Integer from, Integer to, List<Integer> avoidRouteRegionIds) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(esiReaderURL + routePath);
        builder.queryParam("from", from);
        builder.queryParam("to", to);
        builder.queryParam("routeType", "secure");
        if(!avoidRouteRegionIds.isEmpty()) {
            builder.queryParam("avoidRegionIds", avoidRouteRegionIds);
        }
        List<Integer> route = (List<Integer>)restTemplate.getForObject(builder.build().toUriString(), List.class);
        return route != null && !route.isEmpty() ? route : Collections.emptyList();
    }

    public List<RegionOrderStatisticsRestDTO> getOrderStatistics(Integer regionId, Integer typeId) {
        String statisticsPath = MessageFormat.format(statisticsPathWithPlaceHolder, regionId, typeId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(esiReaderURL + statisticsPath);
        List<RegionOrderStatisticsRestDTO> statistics = (List<RegionOrderStatisticsRestDTO>)restTemplate.getForObject(builder.build().toUriString(), List.class);
        return statistics != null && !statistics.isEmpty() ? statistics : Collections.emptyList();
    }
}
package org.mlesyk.marketapi.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mlesyk.marketapi.model.MarketOrder;
import org.mlesyk.marketapi.util.OrdersRestQueryBuilder;
import org.mlesyk.marketapi.util.RouteRestQueryBuilder;
import org.mlesyk.marketapi.util.parameters.OrderType;
import org.mlesyk.marketapi.util.parameters.RouteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MarketRestClient.class, RestTemplate.class, RetryableRequestHandler.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class MarketRestClientTest {

    @Autowired
    MarketRestClient marketRestClient;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void testGetMarketRegionOrderInfoList() {
        Integer regionId = 10000001;
        OrdersRestQueryBuilder ordersRestQueryBuilder = OrdersRestQueryBuilder.getInstance().setRegionId(regionId);
        List<MarketOrder> marketRegionOrders = marketRestClient.getRegionOrderInfoList(ordersRestQueryBuilder.build());
        assertThat(marketRegionOrders).isNotNull();
        assertThat(marketRegionOrders.size()).isGreaterThan(0);
    }

    @Test
    void testGetMarketRegionOrderInfoListWithPages() {
        Integer regionId = 10000001;
        Integer page = 2;
        OrdersRestQueryBuilder ordersRestQueryBuilder = OrdersRestQueryBuilder.getInstance().setRegionId(regionId).setPage(page);
        List<MarketOrder> marketRegionOrders = marketRestClient.getRegionOrderInfoList(ordersRestQueryBuilder.build());
        assertThat(marketRegionOrders).isNotNull();
    }

    @Test
    void testGetMarketRegionOrderInfoListWithOrderType() {
        Integer regionId = 10000001;
        OrderType orderType = OrderType.SELL;
        OrdersRestQueryBuilder ordersRestQueryBuilder = OrdersRestQueryBuilder.getInstance().setRegionId(regionId).setOrderType(orderType);
        List<MarketOrder> marketRegionOrders = marketRestClient.getRegionOrderInfoList(ordersRestQueryBuilder.build());
        assertThat(marketRegionOrders).isNotNull();
        assertThat(marketRegionOrders.stream().anyMatch(e -> e.getIsBuyOrder().equals(Boolean.TRUE))).isFalse();
    }

    @Test
    void testGetMarketRegionOrderInfoListWithTypeId() {
        Integer regionId = 10000001;
        Integer typeId = 4258; //Core Probe Launcher II
        OrdersRestQueryBuilder ordersRestQueryBuilder = OrdersRestQueryBuilder.getInstance().setRegionId(regionId).setTypeId(typeId);
        List<MarketOrder> marketRegionOrders = marketRestClient.getRegionOrderInfoList(ordersRestQueryBuilder.build());
        assertThat(marketRegionOrders).isNotNull();
    }

    @Test
    void testGetMarketTypeIdListInRegion() {
        Integer regionId = 10000001;
        Integer page = 2;
        Integer[] marketTypeIdListInRegion = marketRestClient.getTypeIdListByRegion(regionId, page);
        assertThat(marketTypeIdListInRegion).isNotNull();
    }

    @Test
    void testGetMarketTypeIdListInRegionNonExistentPage() {
        Integer regionId = 10000001;
        Integer page = 222;
        try {
            marketRestClient.getTypeIdListByRegion(regionId, page);
        } catch (HttpStatusCodeException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getResponseBodyAsString()).contains("Requested page does not exist");
            assertThat(e.getMessage()).contains("404 Not Found");
        }
    }

    @Test
    void testCalculateRouteBetweenSystemsShortest() {
        Integer origin = 30000005;
        Integer destination = 30000112;
        Integer[] routePathExpected = new Integer[]{30000005, 30000001, 30000007, 30000008, 30000006, 30000023, 30000030, 30000112};
        Map<String, Object> webParams = RouteRestQueryBuilder.getInstance()
                .setOriginValue(origin)
                .setDestinationValue(destination)
                .setRouteType(RouteType.SHORTEST)
                .build();
        List<Integer> routePath = marketRestClient.calculateRouteBetweenSystems(webParams);
        assertThat(routePath).containsExactly(routePathExpected);
    }

    @Test
    void testCalculateRouteBetweenSystemsShortestWithAvoidList() {
        Integer origin = 30000005;
        Integer destination = 30000112;
        Integer[] routePathExpected = new Integer[]{30000005, 30000032, 30000033, 30000031, 30000035, 30000114, 30000113, 30000112};
        Map<String, Object> webParams = RouteRestQueryBuilder.getInstance()
                .setOriginValue(origin)
                .setDestinationValue(destination)
                .setRouteType(RouteType.SHORTEST)
                .addRegionIdToAvoid(30000001)
                .addRegionIdToAvoid(30000007)
                .build();
        List<Integer> routePath = marketRestClient.calculateRouteBetweenSystems(webParams);
        assertThat(routePath).containsExactly(routePathExpected);
    }

    @Test
    void testCalculateRouteBetweenSystemsShortestWithSystemPair() {
        Integer origin = 30000005;
        Integer destination = 30000112;
        Integer[] routePathExpected = new Integer[]{30000005, 30000001, 30000030, 30000112};
        Map<String, Object> webParams = RouteRestQueryBuilder.getInstance()
                .setOriginValue(origin)
                .setDestinationValue(destination)
                .setRouteType(RouteType.SHORTEST)
                .addConnectedSystemIdPair(30000001, 30000030)
                .build();
        List<Integer> routePath = marketRestClient.calculateRouteBetweenSystems(webParams);
        assertThat(routePath).containsExactly(routePathExpected);
    }

    @Test
    void testCalculateRouteBetweenSystemsShortestWithTwoSystemPair() {
        Integer origin = 30000005;
        Integer destination = 30000112;
        Integer[] routePathExpected = new Integer[]{30000005, 30000001, 30000008, 30000006, 30000030, 30000112};
        Map<String, Object> webParams = RouteRestQueryBuilder.getInstance()
                .setOriginValue(origin)
                .setDestinationValue(destination)
                .setRouteType(RouteType.SHORTEST)
                .addConnectedSystemIdPair(30000001, 30000008)
                .addConnectedSystemIdPair(30000006, 30000030)
                .build();
        List<Integer> routePath = marketRestClient.calculateRouteBetweenSystems(webParams);
        assertThat(routePath).containsExactly(routePathExpected);
    }

    @Test
    void testCalculateRouteBetweenSystemsInsecure() {
        Integer origin = 30000005;
        Integer destination = 30000112;
        Integer[] routePathExpected = new Integer[]{30000005, 30000032, 30000033, 30000031, 30000035, 30000114, 30000113, 30000112};
        Map<String, Object> webParams = RouteRestQueryBuilder.getInstance()
                .setOriginValue(origin)
                .setDestinationValue(destination)
                .setRouteType(RouteType.INSECURE)
                .build();
        List<Integer> routePath = marketRestClient.calculateRouteBetweenSystems(webParams);
        assertThat(routePath).containsExactly(routePathExpected);
    }

    @Test
    void testCalculateRouteBetweenSystemsSecure() {
        Integer origin = 30000005;
        Integer destination = 30000112;
        Integer[] routePathExpected = new Integer[]{30000005, 30000001, 30000007, 30000008, 30000006, 30000023, 30000030, 30000112};
        Map<String, Object> webParams = RouteRestQueryBuilder.getInstance()
                .setOriginValue(origin)
                .setDestinationValue(destination)
                .setRouteType(RouteType.SECURE)
                .build();
        List<Integer> routePath = marketRestClient.calculateRouteBetweenSystems(webParams);
        assertThat(routePath).containsExactly(routePathExpected);
    }

    @Test
    void testEsiHeaders() {
        HttpHeaders httpHeaders = restTemplate.headForHeaders("https://esi.evetech.net/latest/universe/regions/");
        for(Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            System.out.println(entry);
        }
    }
}

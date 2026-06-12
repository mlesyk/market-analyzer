package org.mlesyk.marketapi.service;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.marketapi.client.MarketRestClient;
import org.mlesyk.marketapi.client.StaticDataServiceRestClient;
import org.mlesyk.marketapi.model.MarketOrder;
import org.mlesyk.marketapi.model.MarketOrderStatistics;
import org.mlesyk.marketapi.model.Region;
import org.mlesyk.marketapi.util.OrdersRestQueryBuilder;
import org.mlesyk.marketapi.util.RouteRestQueryBuilder;
import org.mlesyk.marketapi.util.parameters.RouteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class MarketOrdersService {

    private final MarketRestClient marketRestClient;
    private final StaticDataServiceRestClient staticDataServiceRestClient;

    @Autowired
    public MarketOrdersService(MarketRestClient marketRestClient, StaticDataServiceRestClient staticDataServiceRestClient) {
        this.marketRestClient = marketRestClient;
        this.staticDataServiceRestClient = staticDataServiceRestClient;
    }

    ExecutorService httpExecutor = Executors.newFixedThreadPool(10);

    public Set<MarketOrder> getOrdersByRegionId(Integer regionId) {
        Set<Integer> allItemIds = staticDataServiceRestClient.getAllItemIds();

        MarketRestClient.OrdersPage firstPage = marketRestClient.getRegionOrdersPage(
                OrdersRestQueryBuilder.getInstance().setRegionId(regionId).setPage(1).build());
        int totalPages = firstPage.totalPages();
        log.debug("Region {} has {} pages of orders", regionId, totalPages);

        Set<MarketOrder> orders = new HashSet<>(filterByKnownTypes(firstPage.orders(), allItemIds));

        if (totalPages <= 1) {
            log.info("Finished reading orders of region {}, found {} orders, number of pages = {}", regionId, orders.size(), totalPages);
            return orders;
        }

        CompletionService<List<MarketOrder>> completionService = new ExecutorCompletionService<>(httpExecutor);
        for (int page = 2; page <= totalPages; page++) {
            int pageToFetch = page;
            completionService.submit(() -> readSingleOrderPage(regionId, pageToFetch, allItemIds));
        }
        for (int i = 2; i <= totalPages; i++) {
            try {
                orders.addAll(completionService.take().get());
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while waiting", e);
            }
        }
        log.info("Finished reading orders of region {}, found {} orders, number of pages = {}", regionId, orders.size(), totalPages);
        return orders;
    }

    public List<Integer> getTypeIdsByRegionId(Integer regionId) {
        List<Integer> typeIDList = new ArrayList<>();
        boolean isLastPage = false;
        int page = 1;
        while (!isLastPage) {
            try {
                Integer[] typeIdsPage = marketRestClient.getTypeIdListByRegion(regionId, page);
                log.debug("Read finished of typeIds page {} of region {}, found {} types", page, regionId, typeIdsPage.length);
                typeIDList.addAll(Arrays.asList(typeIdsPage));
                page++;
            } catch (HttpStatusCodeException e) {
                isLastPage = true;
            }
        }
        return typeIDList;
    }

    public List<MarketOrderStatistics> getOrderStatisticsByRegionId(Integer regionId, Integer typeId) {
        return marketRestClient.getMarketRegionOrderStatisticsInfoList(regionId, typeId);
    }

    public List<Integer> getUniverseRegionIds() {
        return Arrays.asList(marketRestClient.getUniverseRegionIds());
    }

    public Region getUniverseRegionById(Integer regionId) {
        return marketRestClient.getUniverseRegionInfo(regionId);
    }

    public List<Integer> getRouteBetweenSystems(Integer origin, Integer destination, RouteType routeType, List<Integer> avoidRegionIds) {
        Map<String, Object> webParams = RouteRestQueryBuilder.getInstance()
                .setOriginValue(origin)
                .setDestinationValue(destination)
                .setRouteType(routeType)
                .addRegionIdsToAvoid(avoidRegionIds)
                .build();
        return marketRestClient.calculateRouteBetweenSystems(webParams);
    }

    private List<MarketOrder> readSingleOrderPage(Integer regionId, int page, Set<Integer> allItemIds) {
        List<MarketOrder> regionOrders = marketRestClient.getRegionOrderInfoList(
                OrdersRestQueryBuilder.getInstance().setRegionId(regionId).setPage(page).build());
        log.debug("Read finished of orders page {} of region {}, found {} orders", page, regionId, regionOrders.size());
        return filterByKnownTypes(regionOrders, allItemIds);
    }

    private List<MarketOrder> filterByKnownTypes(List<MarketOrder> orders, Set<Integer> allItemIds) {
        List<MarketOrder> filtered = new ArrayList<>(orders.size());
        for (MarketOrder order : orders) {
            if (allItemIds.contains(order.getTypeId())) {
                filtered.add(order);
            } else {
                log.debug("Order has unknown type id, possibly item from server Serenity: {}", order);
            }
        }
        return filtered;
    }
}

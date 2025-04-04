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
import org.springframework.web.client.HttpClientErrorException;
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
        Set<MarketOrder> orders = new HashSet<>();

        CompletionService<Set<MarketOrder>> completionService = new ExecutorCompletionService<>(httpExecutor);
        List<Callable<Set<MarketOrder>>> tasks = new ArrayList<>();
        Integer regionOrdersMaxPage = findRegionOrderPagesAmount(10, 10, regionId);
        for (int i = 1; i <= regionOrdersMaxPage; i += 10) {
            int startPage = i;
            Callable<Set<MarketOrder>> callableObj = () -> readOrderPagesFromRegion(regionId, startPage, startPage + 10);
            tasks.add(callableObj);
        }
        for (Callable<Set<MarketOrder>> callable : tasks) {
            completionService.submit(callable);
        }
        log.debug("Region {} scheduled read of {} pages", regionId, regionOrdersMaxPage);
        for (int t = 0, n = tasks.size(); t < n; t++) {
            try {
                Future<Set<MarketOrder>> f = completionService.take();
                orders.addAll(f.get());
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while waiting", e);
            }
        }
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

    public Set<MarketOrder> readOrderPagesFromRegion(Integer regionId, int startPage, int lastPage) {
        Set<MarketOrder> orders = new HashSet<>();
        Set<Integer> allItemIds = staticDataServiceRestClient.getAllItemIds();
        int page = startPage;
        while (page <= lastPage) {
            OrdersRestQueryBuilder regionOrderQueryBuilder = OrdersRestQueryBuilder.getInstance().setRegionId(regionId).setPage(page);
            List<MarketOrder> regionOrders = new ArrayList<>(marketRestClient.getRegionOrderInfoList(regionOrderQueryBuilder.build()));
            log.debug("Read finished of orders page {} of region {}, found {} orders", page, regionId, regionOrders.size());
            if(regionOrders.isEmpty()) {
                break;
            }
            Iterator<MarketOrder> iterator = regionOrders.iterator();
            while (iterator.hasNext()) {
                MarketOrder order = iterator.next();
                boolean typeIsCorrect = allItemIds.contains(order.getTypeId());
                if (!typeIsCorrect) {
                    log.debug("Order has unknown type id, possibly item from server Serenity: {}", order);
                    iterator.remove();
                }
            }
            orders.addAll(regionOrders);
            page++;
        }
        log.info("Finished reading orders of region {}, found {} orders, number of pages = {}", regionId, orders.size(), page);
        return orders;
    }

    /**
     * Finds the total number of pages of market orders for a given region.
     * This method uses a recursive approach to determine the number of pages by
     * querying the market orders and adjusting the page and step values based on
     * the presence or absence of orders.
     *
     * @param page the current page number to query
     * @param step the step size to adjust the page number
     * @param regionId the ID of the region to query
     * @return the total number of pages of market orders for the given region
     */
    public Integer findRegionOrderPagesAmount(Integer page, Integer step, Integer regionId) {
        if (step < 10) {
            return page + step;
        }
        OrdersRestQueryBuilder regionOrderQueryBuilder = OrdersRestQueryBuilder.getInstance().setRegionId(regionId).setPage(page);
        List<MarketOrder> orders = marketRestClient.getRegionOrderInfoList(regionOrderQueryBuilder.build());
        if (orders.isEmpty()) {
            if (page.equals(step)) {
                if (page.equals(10)) {
                    return page;
                }
                return findRegionOrderPagesAmount(page - step / 4, step / 4, regionId);
            } else {
                return findRegionOrderPagesAmount(page - step / 2, step / 2, regionId);
            }
        } else {
            if (page.equals(step)) {
                return findRegionOrderPagesAmount(page + step, step * 2, regionId);
            } else {
                return findRegionOrderPagesAmount(page + step / 2, step / 2, regionId);
            }
        }
    }
}
package org.mlesyk.marketapi.controller;

import org.mlesyk.marketapi.model.MarketOrder;
import org.mlesyk.marketapi.model.MarketOrderStatistics;
import org.mlesyk.marketapi.model.Region;
import org.mlesyk.marketapi.service.MarketOrdersService;
import org.mlesyk.marketapi.util.parameters.RouteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class MarketOrdersController {

    private final MarketOrdersService marketOrdersService;

    @Autowired
    public MarketOrdersController(MarketOrdersService marketOrdersService) {
        this.marketOrdersService = marketOrdersService;
    }

    @GetMapping("/regions")
    public List<Integer> getUniverseRegionIds() {
        return marketOrdersService.getUniverseRegionIds();
    }

    @GetMapping("/regions/{regionId}")
    public Region getUniverseRegionById(@PathVariable Integer regionId) {
        return marketOrdersService.getUniverseRegionById(regionId);
    }

    @GetMapping("/regions/{regionId}/orders")
    public Set<MarketOrder> getOrdersByRegionId(@PathVariable Integer regionId) {
        return marketOrdersService.getOrdersByRegionId(regionId);
    }

    @GetMapping("/regions/{regionId}/orders/types")
    public List<Integer> getTypeIdsByRegionId(@PathVariable Integer regionId) {
        return marketOrdersService.getTypeIdsByRegionId(regionId);
    }

    @GetMapping("/regions/{regionId}/orders/types/{typeId}/statistics")
    public List<MarketOrderStatistics> getOrderStatisticsByRegionId(@PathVariable Integer regionId,
                                                                    @PathVariable Integer typeId) {
        return marketOrdersService.getOrderStatisticsByRegionId(regionId, typeId);
    }

    // http://localhost:8081/route?from=30000005&to=30000112&routeType=shortest&avoidRegionIds=30000001,30000007
    @GetMapping("/route")
    public List<Integer> getRouteBetweenSystems(@RequestParam Integer from,
                                                @RequestParam Integer to,
                                                @RequestParam(required = false) String routeType,
                                                @RequestParam(required = false) List<Integer> avoidRegionIds) {
        try {
            RouteType routeTypeEnum = RouteType.fromString(routeType);
            return marketOrdersService.getRouteBetweenSystems(from, to, routeTypeEnum, avoidRegionIds);
        } catch (IllegalArgumentException e) {
            return marketOrdersService.getRouteBetweenSystems(from, to, RouteType.SHORTEST, avoidRegionIds);
        }
    }
}

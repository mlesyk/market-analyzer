package org.mlesyk.orders.profitable.controller;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.profitable.model.ProfitableOrders;
import org.mlesyk.orders.profitable.model.rest.MarketTypeNameToIdDTO;
import org.mlesyk.orders.profitable.model.rest.ProfitableOrdersViewDTO;
import org.mlesyk.orders.profitable.repository.ProfitableOrdersRepository;
import org.mlesyk.orders.profitable.util.OrderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@Slf4j
public class ProfitableOrdersController {

    private final ProfitableOrdersRepository profitableOrdersRepository;
    private final OrderUtils orderUtils;

    @Autowired
    public ProfitableOrdersController(ProfitableOrdersRepository profitableOrdersRepository, OrderUtils orderUtils) {
        this.profitableOrdersRepository = profitableOrdersRepository;
        this.orderUtils = orderUtils;
    }

    List<ProfitableOrdersViewDTO> profitableOrdersView = Collections.synchronizedList(new ArrayList<>());

    List<MarketTypeNameToIdDTO> typeIdToNameMapping = Collections.synchronizedList(new ArrayList<>());

    @GetMapping("/orders")
    public List<ProfitableOrdersViewDTO> getProfitableOrders(@RequestParam(required = false) List<String> avoidOrdersWithSystems,
                                                             @RequestParam(required = false) List<Integer> avoidTypeIds,
                                                             @RequestParam(required = false) List<Integer> avoidRouteRegionIds,
                                                             @RequestParam(required = false) String name,
                                                             @RequestParam(required = false) Long minProfit,
                                                             @RequestParam(required = false) Long maxVolume,
                                                             @RequestParam(required = false) Long maxJumpsSafeNS,
                                                             @RequestParam(required = false) Long maxJumpsSafeLS,
                                                             @RequestParam(required = false) Long maxJumpsSafe,
                                                             @RequestParam(required = false) Long maxJumpsShort,
                                                             @RequestParam(required = false) Long minProfitPerCubicMeter,
                                                             @RequestParam(required = false) Long minProfitPerJumpSafe,
                                                             @RequestParam(required = false) Long minProfitPerJumpShort) {
        if(log.isDebugEnabled()) {
            log.debug("Find profitable orders with:" +
                    " avoidOrdersWithRegionIds " + avoidOrdersWithSystems +
                    " avoidTypeIds " + avoidTypeIds +
                    " avoidRouteRegionIds " + avoidRouteRegionIds +
                    " name " + name +
                    " minProfit " + minProfit +
                    " maxVolume " + maxVolume +
                    " maxJumpsSafeNS " + maxJumpsSafeNS +
                    " maxJumpsSafeLS " + maxJumpsSafeLS +
                    " maxJumpsSafe " + maxJumpsSafe +
                    " maxJumpsShort " + maxJumpsShort +
                    " minProfitPerCubicMeter " + minProfitPerCubicMeter +
                    " minProfitPerJumpSafe " + minProfitPerJumpSafe +
                    " minProfitPerJumpShort " + minProfitPerJumpShort);
        }
        List<ProfitableOrdersViewDTO> profitableOrdersViewResult = null;
        if(avoidRouteRegionIds != null && !avoidRouteRegionIds.isEmpty()) {
            Iterable<ProfitableOrders> orders = profitableOrdersRepository.findAll();
            List<ProfitableOrders> profitableOrders = StreamSupport.stream(orders.spliterator(), false).toList();
            profitableOrdersViewResult = orderUtils.fillProfitableOrdersViewDTO(profitableOrders, avoidRouteRegionIds);
        } else {
            profitableOrdersViewResult = new ArrayList<>(profitableOrdersView);
        }
        if(avoidTypeIds != null && !avoidTypeIds.isEmpty()) {
            Set<Integer> avoidTypeIdsSet = new HashSet<>(avoidTypeIds);
            profitableOrdersViewResult = profitableOrdersViewResult.stream().filter(e -> !avoidTypeIdsSet.contains(e.getMarketItemID())).toList();
        }
        if(avoidOrdersWithSystems != null && !avoidOrdersWithSystems.isEmpty()) {
            Set<String> avoidOrdersWithRegionIdsSet = new HashSet<>(avoidOrdersWithSystems);
            profitableOrdersViewResult = profitableOrdersViewResult.stream().filter(e -> !avoidOrdersWithRegionIdsSet.contains(e.getBuyOrderSystem())).toList();
            profitableOrdersViewResult = profitableOrdersViewResult.stream().filter(e -> !avoidOrdersWithRegionIdsSet.contains(e.getSellOrderSystem())).toList();
        }

        if(name != null && !name.isEmpty()) {
            profitableOrdersViewResult = profitableOrdersViewResult.stream().filter(e -> e.getMarketItemName().toLowerCase().contains(name.toLowerCase())).toList();
        }
        if(minProfit != null && minProfit != 0) {
            profitableOrdersViewResult = profitableOrdersViewResult.stream().filter(e -> e.getTotalNetProfit() > minProfit).toList();
        }
        if(maxVolume != null && maxVolume != 0) {
            profitableOrdersViewResult = profitableOrdersViewResult.stream()
                    .filter(e -> e.getBuyOrderVolumeRemain() < maxVolume || e.getSellOrderVolumeRemain() < maxVolume).toList();
        }
        if(maxJumpsSafeNS != null && maxJumpsSafeNS != 0) {
            profitableOrdersViewResult = profitableOrdersViewResult.stream().filter(e -> e.getNullSecJumps() < maxJumpsSafeNS).toList();
        }
        if(maxJumpsSafeLS != null && maxJumpsSafeLS != 0) {
            profitableOrdersViewResult = profitableOrdersViewResult.stream().filter(e -> e.getLowSecJumps() < maxJumpsSafeLS).toList();
        }
        if(maxJumpsSafe != null && maxJumpsSafe != 0) {
            profitableOrdersViewResult = profitableOrdersViewResult.stream().filter(e -> e.getTotalJumps() < maxJumpsSafe).toList();
        }
        if(maxJumpsShort != null && maxJumpsShort != 0) {
            profitableOrdersViewResult = profitableOrdersViewResult.stream().filter(e -> e.getTotalJumpsShortest() < maxJumpsShort).toList();
        }
        return profitableOrdersViewResult;
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    public void profitableOrdersUpdater() {
        if(typeIdToNameMapping.isEmpty()) {
            typeIdToNameMapping = orderUtils.getMarketTypeNameToIdMapping();
        }
        Iterable<ProfitableOrders> orders = profitableOrdersRepository.findAll();
        List<ProfitableOrders> profitableOrders = StreamSupport.stream(orders.spliterator(), false).toList();
        log.info("ProfitableOrders from DB: {}", profitableOrders.size());
        HashSet<ProfitableOrders> profitableOrdersSet = new HashSet<>(profitableOrders);
        // filter and explore only new orders
        profitableOrdersSet.retainAll(profitableOrders.stream().filter(profitableOrder -> profitableOrdersView.stream().noneMatch(orderView -> profitableOrder.getBuyOrderIdPK().equals(orderView.getBuyOrderId())
                && profitableOrder.getSellOrderIdPK().equals(orderView.getSellOrderId()))).collect(Collectors.toSet()));
        List<ProfitableOrders> newProfitableOrders = new ArrayList<>(profitableOrdersSet);
        log.info("new ProfitableOrders for provision: {}", newProfitableOrders.size());
        profitableOrdersView.addAll(orderUtils.fillProfitableOrdersViewDTO(newProfitableOrders, Collections.emptyList()));
        // delete orders which does not exist anymore
        profitableOrdersView = profitableOrdersView.stream().filter(orderView -> profitableOrders.stream().anyMatch(profitableOrder -> profitableOrder.getBuyOrderIdPK().equals(orderView.getBuyOrderId())
                && profitableOrder.getSellOrderIdPK().equals(orderView.getSellOrderId()))).collect(Collectors.toList());
        log.info("ProfitableOrders view cache: {}", profitableOrdersView.stream().map(ProfitableOrdersViewDTO::getMarketItemID).toList());
    }
}

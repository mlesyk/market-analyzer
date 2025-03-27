package org.mlesyk.orders.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.analyzer.repository.RegionOrderRepository;
import org.mlesyk.orders.analyzer.repository.dto.RegionOrderCalculatedValueDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderService {

    private final RegionOrderRepository regionOrderRepository;

    @Autowired
    public OrderService(RegionOrderRepository regionOrderRepository) {
        this.regionOrderRepository = regionOrderRepository;
    }

    @Value("${app.orders.sell_order_prise_threshold}")
    Double sellOrderPriceThreshold;

    @Value("${app.orders.buy_order_prise_threshold}")
    Double buyOrderPriceThreshold;

    public List<RegionOrderCalculatedValueDTO> getValuableBuyOrdersByTypeId(Double profitAmount, Integer typeId) {
        long timeBefore = System.currentTimeMillis();
        List<RegionOrderCalculatedValueDTO> orders = regionOrderRepository.findValuableBuyOrdersByTypeId(profitAmount * buyOrderPriceThreshold, typeId);
        long timeAfter = System.currentTimeMillis();
        log.debug("typeId: {} getValuableBuyOrders count: {} time elapsed: {}", typeId, orders.size(), (timeAfter - timeBefore) / 1000);

        return orders;
    }

    public List<RegionOrderCalculatedValueDTO> getValuableSellOrdersByTypeId(Double profitAmount, Integer typeId) {
        long timeBefore = System.currentTimeMillis();
        List<RegionOrderCalculatedValueDTO> orders = regionOrderRepository.findValuableSellOrdersByTypeId(profitAmount * sellOrderPriceThreshold, typeId);
        long timeAfter = System.currentTimeMillis();
        log.debug("typeId: {} getValuableSellOrders count: {} time elapsed: {}", typeId, orders.size(), (timeAfter - timeBefore) / 1000);
        return orders;
    }

    public List<RegionOrderCalculatedValueDTO> getValuableBuyOrdersByTypeIdList(Double profitAmount, List<Integer> typeIds) {
        long timeBefore = System.currentTimeMillis();
        List<RegionOrderCalculatedValueDTO> orders = regionOrderRepository.findValuableBuyOrdersByTypeIdList(profitAmount * buyOrderPriceThreshold, typeIds);
        long timeAfter = System.currentTimeMillis();
        log.debug("typeId list size: {} getValuableBuyOrders count: {} time elapsed: {}", typeIds.size(), orders.size(), (timeAfter - timeBefore) / 1000);

        return orders;
    }

    public List<RegionOrderCalculatedValueDTO> getValuableSellOrdersByTypeIdList(Double profitAmount, List<Integer> typeIds) {
        long timeBefore = System.currentTimeMillis();
        List<RegionOrderCalculatedValueDTO> orders = regionOrderRepository.findValuableSellOrdersByTypeIdList(profitAmount * sellOrderPriceThreshold, typeIds);
        long timeAfter = System.currentTimeMillis();
        log.debug("typeId list size: {} getValuableSellOrders count: {} time elapsed: {}", typeIds.size(), orders.size(), (timeAfter - timeBefore) / 1000);
        return orders;
    }

    public List<Integer> getValuableOrderTypeIds(Double profitAmount) {
        return regionOrderRepository.findDistinctValuableOrderTypesId(profitAmount);
    }
}

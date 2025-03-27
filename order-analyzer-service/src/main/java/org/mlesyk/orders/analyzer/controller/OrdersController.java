package org.mlesyk.orders.analyzer.controller;

import jakarta.persistence.Tuple;
import org.mlesyk.orders.analyzer.repository.RegionOrderRepository;
import org.mlesyk.orders.analyzer.repository.dto.RegionOrderAvgPriceCalculatedDTO;
import org.mlesyk.orders.analyzer.repository.dto.RegionOrderCalculatedValueDTO;
import org.mlesyk.orders.analyzer.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrdersController {

    private final OrderService orderService;
    private final RegionOrderRepository repository;

    @Autowired
    public OrdersController(OrderService orderService, RegionOrderRepository repository) {
        this.orderService = orderService;
        this.repository = repository;
    }

    @GetMapping("/getValuableBuyOrders")
    public List<RegionOrderCalculatedValueDTO> getValuableBuyOrders(@RequestParam Double profitAmount, @RequestParam Integer typeId) {
        return orderService.getValuableBuyOrdersByTypeId(profitAmount, typeId);
    }

    @GetMapping("/getValuableSellOrders")
    public List<RegionOrderCalculatedValueDTO> getValuableSellOrders(@RequestParam Double profitAmount, @RequestParam Integer typeId) {
        return orderService.getValuableSellOrdersByTypeId(profitAmount, typeId);
    }

    @GetMapping("/getValuableOrderTypeIds")
    public List<Integer> getValuableOrderTypeIds(@RequestParam Double profitAmount) {
        return orderService.getValuableOrderTypeIds(profitAmount);
    }

    @GetMapping("/getSellOrdersAvgPrices")
    public List<RegionOrderAvgPriceCalculatedDTO> getSellOrdersAvgPrices() {
        List<Tuple> sellOrdersAvgPriceRawData = repository.findSellOrdersAvgPrice();
        return sellOrdersAvgPriceRawData.stream()
                .map(e -> new RegionOrderAvgPriceCalculatedDTO(e.get(0, Integer.class), e.get(1, Double.class)))
                .collect(Collectors.toList());
    }

    @GetMapping("/getBuyOrdersAvgPrices")
    public List<RegionOrderAvgPriceCalculatedDTO> getBuyOrdersAvgPrices() {
        List<Tuple> buyOrdersAvgPriceRawData = repository.findBuyOrdersAvgPrice();
        return buyOrdersAvgPriceRawData.stream()
                .map(e -> new RegionOrderAvgPriceCalculatedDTO(e.get(0, Integer.class), e.get(1, Double.class)))
                .collect(Collectors.toList());
    }

}

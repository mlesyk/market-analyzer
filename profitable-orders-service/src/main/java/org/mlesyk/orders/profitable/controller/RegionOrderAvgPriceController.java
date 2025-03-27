package org.mlesyk.orders.profitable.controller;

import org.mlesyk.orders.profitable.client.MarketApiReaderClient;
import org.mlesyk.orders.profitable.client.OrderStorageServiceClient;
import org.mlesyk.orders.profitable.model.RegionOrderAvgPriceCalculated;
import org.mlesyk.orders.profitable.model.RegionOrderStatistics;
import org.mlesyk.orders.profitable.model.rest.RegionOrderAvgPriceCalculatedDTO;
import org.mlesyk.orders.profitable.model.rest.RegionOrderStatisticsRestDTO;
import org.mlesyk.orders.profitable.repository.RegionOrderAvgPriceRepository;
import org.mlesyk.orders.profitable.repository.RegionOrderStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegionOrderAvgPriceController {

    private final RegionOrderAvgPriceRepository avgPriceRepository;
    private final RegionOrderStatisticsRepository statisticsRepository;
    private final OrderStorageServiceClient orderStorageServiceClient;
    private final MarketApiReaderClient marketApiReaderClient;

    @Autowired
    public RegionOrderAvgPriceController(RegionOrderAvgPriceRepository avgPriceRepository,
                                         RegionOrderStatisticsRepository statisticsRepository,
                                         OrderStorageServiceClient orderStorageServiceClient,
                                         MarketApiReaderClient marketApiReaderClient) {
        this.avgPriceRepository = avgPriceRepository;
        this.statisticsRepository = statisticsRepository;
        this.orderStorageServiceClient = orderStorageServiceClient;
        this.marketApiReaderClient = marketApiReaderClient;
    }

    @GetMapping("/storageClientAvgPrices/buyOrders")
    public List<RegionOrderAvgPriceCalculatedDTO> getBuyOrdersAvgPrices() {
        return orderStorageServiceClient.getBuyOrdersAvgPrices();
    }

    @GetMapping("/storageClientAvgPrices/sellOrders")
    public List<RegionOrderAvgPriceCalculatedDTO> getSellOrdersAvgPrices() {
        return orderStorageServiceClient.getSellOrdersAvgPrices();
    }

    @GetMapping("/esiReaderClient/statistics")
    public List<RegionOrderStatisticsRestDTO> getSellOrdersAvgPrices(@RequestParam Integer regionId, @RequestParam Integer typeId) {
        return marketApiReaderClient.getOrderStatistics(regionId, typeId);
    }

    @GetMapping("/avgPriceRepository")
    public List<RegionOrderAvgPriceCalculated> getOrdersAvgPrices(@RequestParam Integer typeId) {
        return avgPriceRepository.findAllByTypeId(typeId);
    }


    @GetMapping("/statisticsRepository")
    public List<RegionOrderStatistics> getOrdersStatistics(@RequestParam Integer typeId) {
        return statisticsRepository.findAllByTypeId(typeId);
    }

}
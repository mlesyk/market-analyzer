package org.mlesyk.orders.profitable.service;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.profitable.client.OrderStorageServiceClient;
import org.mlesyk.orders.profitable.model.RegionOrderAvgPriceCalculated;
import org.mlesyk.orders.profitable.model.rest.RegionOrderAvgPriceCalculatedDTO;
import org.mlesyk.orders.profitable.repository.RegionOrderAvgPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegionOrderAvgPriceConsumerService {

    private final OrderStorageServiceClient orderStorageClient;
    private final RegionOrderAvgPriceRepository repository;

    @Autowired
    public RegionOrderAvgPriceConsumerService(OrderStorageServiceClient orderStorageClient, RegionOrderAvgPriceRepository repository) {
        this.orderStorageClient = orderStorageClient;
        this.repository = repository;
    }

    @Value("${app.services.avg_price_enabled}")
    boolean avgPriceEnabled;

    @Scheduled(cron = "*/5 * * * * *")
    public void consumerScheduler() {
        if (avgPriceEnabled) {
            List<RegionOrderAvgPriceCalculatedDTO> buyOrdersAvgPrices = orderStorageClient.getBuyOrdersAvgPrices();
            if (!buyOrdersAvgPrices.isEmpty()) {
                repository.saveAll(buyOrdersAvgPrices.stream().map(e -> new RegionOrderAvgPriceCalculated(e, true)).toList());
            }

            List<RegionOrderAvgPriceCalculatedDTO> sellOrdersAvgPrices = orderStorageClient.getSellOrdersAvgPrices();
            if (!sellOrdersAvgPrices.isEmpty()) {
                repository.saveAll(sellOrdersAvgPrices.stream().map(e -> new RegionOrderAvgPriceCalculated(e, false)).toList());
            }
        }
    }
}

package org.mlesyk.orders.analyzer.service;

import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.analyzer.model.rest.ProfitableOrdersDTO;
import org.mlesyk.orders.analyzer.repository.dto.RegionOrderCalculatedValueDTO;
import org.mlesyk.orders.analyzer.util.TaxCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaValuableOrdersProducerService {

    @Value("${app.orders.profit_amount_value}")
    Double profitAmount;

    private final OrderService orderService;
    private final TaxCalculator taxCalculator;
    private final KafkaTemplate<Integer, ProfitableOrdersDTO> profitableOrdersKafkaTemplate;

    ThreadPoolExecutor executor;

    @Autowired
    public KafkaValuableOrdersProducerService(@Value("${app.orders.producer.pool_size}") Integer poolSize,
                                              OrderService orderService,
                                              TaxCalculator taxCalculator,
                                              KafkaTemplate<Integer, ProfitableOrdersDTO> profitableOrdersKafkaTemplate) {
        this.executor = new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        this.orderService = orderService;
        this.taxCalculator = taxCalculator;
        this.profitableOrdersKafkaTemplate = profitableOrdersKafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${app.orders.producer.scheduler_delay}", initialDelayString = "${app.orders.producer.scheduler_initial_delay}")
    public void sendValuableOrders() {
        Set<Integer> valuableOrderTypeIds = new HashSet<>(orderService.getValuableOrderTypeIds(profitAmount));
        log.info("Valuable market types count : {}", valuableOrderTypeIds.size());

        if (valuableOrderTypeIds.size() > 500) {
            int batchesSize = 500;
            Iterable<List<Integer>> batchList = Iterables.partition(valuableOrderTypeIds, batchesSize);
            for (List<Integer> batch : batchList) {
                processBatch(batch);
            }
        } else {
            processBatch(new ArrayList<>(valuableOrderTypeIds));
        }
        try {
            while (executor.getActiveCount() > 0) {
                TimeUnit.MILLISECONDS.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
        }
    }

    public Double calculateBuySellOrderPairProfit(Double sellOrderPrice, Integer sellOrderVolume, Double buyOrderPrice, Integer buyOrderVolume) {
        int transactionVolume = Math.min(sellOrderVolume, buyOrderVolume);
        return (buyOrderPrice * (1.0 - taxCalculator.getSaleTax(0)) - sellOrderPrice) * transactionVolume;
    }

    public void processBatch(List<Integer> batch) {
        executor.execute(() -> {
            List<RegionOrderCalculatedValueDTO> valuableBuyOrders = orderService.getValuableBuyOrdersByTypeIdList(profitAmount, batch);
            List<RegionOrderCalculatedValueDTO> valuableSellOrders = orderService.getValuableSellOrdersByTypeIdList(profitAmount, batch);

            if (!valuableBuyOrders.isEmpty() && !valuableSellOrders.isEmpty()) {
                Set<Integer> valuableBuyOrdersTypeIds = valuableBuyOrders.stream().map(RegionOrderCalculatedValueDTO::getTypeId).collect(Collectors.toSet());
                Set<Integer> valuableSellOrdersTypeIds = valuableSellOrders.stream().map(RegionOrderCalculatedValueDTO::getTypeId).collect(Collectors.toSet());
                valuableBuyOrdersTypeIds.retainAll(valuableSellOrdersTypeIds);
                for(Integer typeId: valuableBuyOrdersTypeIds) {
                    List<RegionOrderCalculatedValueDTO> valuableBuyOrdersFiltered = valuableBuyOrders.stream().filter(e -> typeId.equals(e.getTypeId())).toList();
                    List<RegionOrderCalculatedValueDTO> valuableSellOrdersFiltered = valuableSellOrders.stream().filter(e -> typeId.equals(e.getTypeId())).toList();
                    log.debug("Valuable market type {} buy orders count : {}", typeId, valuableBuyOrdersFiltered.size());
                    log.debug("Valuable market type {} sell orders count : {}", typeId, valuableSellOrdersFiltered.size());
                    for (RegionOrderCalculatedValueDTO sellOrder : valuableSellOrdersFiltered) {
                        List<RegionOrderCalculatedValueDTO> profitableBuyOrders = valuableBuyOrdersFiltered.stream()
                                .filter(e -> calculateBuySellOrderPairProfit(sellOrder.getPrice(), sellOrder.getVolumeRemain(), e.getPrice(), e.getVolumeRemain()) > profitAmount)
                                .toList();
                        if (!profitableBuyOrders.isEmpty()) {
                            for (RegionOrderCalculatedValueDTO buyOrder : profitableBuyOrders) {
                                profitableOrdersKafkaTemplate.send("profitable_orders", typeId, new ProfitableOrdersDTO(typeId, buyOrder, sellOrder));
                            }
                        }
                    }
                }
            }
        });
    }
}

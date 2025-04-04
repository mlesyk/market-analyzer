package org.mlesyk.marketapi.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mlesyk.marketapi.model.MarketOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaOrdersProducerService {

    private final MarketOrdersService marketOrdersService;
    private final KafkaTemplate<Long, MarketOrder> ordersKafkaTemplate;
    private final KafkaTemplate<Long, Long> ordersRegistryKafkaTemplate;

    private final ThreadPoolExecutor executor;

    private final Cache<Long, Long> cache;

    // for debug purposes to limit run count, default - no limits
    @Value("${app.market.orders.producer.run_count}")
    Integer runCount;

    Integer counter = 0;
    long sleepTimeSeconds = 10;

    @Autowired
    public KafkaOrdersProducerService(@Value("${app.market.orders.producer.kafka_orders_producer_pool_size}") Integer poolSize,
                                      @Value("${app.market.orders.producer.cache_life_minutes}") Integer cacheLifetime,
                                      MarketOrdersService marketOrdersService,
                                      KafkaTemplate<Long, MarketOrder> ordersKafkaTemplate,
                                      KafkaTemplate<Long, Long> ordersRegistryKafkaTemplate) {
        this.executor = new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheLifetime, TimeUnit.MINUTES) // Set a TTL of 120 minutes
                .build();
        this.marketOrdersService = marketOrdersService;
        this.ordersKafkaTemplate = ordersKafkaTemplate;
        this.ordersRegistryKafkaTemplate = ordersRegistryKafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${app.market.orders.producer.scheduler_delay}", initialDelayString = "${app.market.orders.producer.scheduler_initial_delay}")
    public void sendOrders() {
        if (runCount.equals(0) || counter < runCount) {
            List<Integer> regions = marketOrdersService.getUniverseRegionIds();
            for (Integer region : regions) {
                log.info("scheduling for region {}", region);
                executor.execute(() -> processRegion(region));
            }
            log.debug("before waitForAllTasksToComplete");
            waitForAllTasksToComplete();
            log.debug("after waitForAllTasksToComplete finish");
            if (!runCount.equals(0)) {
                counter++;
            }
        }
    }

    private void processRegion(Integer region) {
        log.debug("Reading orders in system {}", region);

        Set<MarketOrder> orders = marketOrdersService.getOrdersByRegionId(region);
        ConcurrentMap<Long, Long> cacheMap = cache.asMap();

        log.info("Region {} has {} total orders", region, orders.size());

        Set<MarketOrder> newOrders = orders.stream()
                .filter(order -> !cacheMap.containsKey(order.getOrderId()))
                .collect(Collectors.toSet());

        log.info("Region {} has {} new orders", region, newOrders.size());
        sendNewOrdersToKafka(newOrders);
        updateOrderRegistry(orders);

    }

    // Orders should be sent once a cache timeout to minimize load on database with overwrites
    private void sendNewOrdersToKafka(Set<MarketOrder> newOrders) {
        for (MarketOrder order : newOrders) {
            cache.put(order.getOrderId(), order.getOrderId());
            ordersKafkaTemplate.send("orders", order.getOrderId(), order);
        }
    }

    // Orders registry should always contain orders ID updated to see active orders
    private void updateOrderRegistry(Set<MarketOrder> orders) {
        for (MarketOrder order : orders) {
            ordersRegistryKafkaTemplate.send("handler_orders_registry", order.getOrderId(), order.getOrderId());
            ordersRegistryKafkaTemplate.send("storage_orders_registry", order.getOrderId(), order.getOrderId());
        }
    }

    private void waitForAllTasksToComplete() {
        try {
            while (executor.getActiveCount() > 0) {
                log.debug("executor.toString {}", executor.toString());
                log.debug("executor.getActiveCount {}", executor.getActiveCount());
                log.debug("executor.getCompletedTaskCount {}", executor.getCompletedTaskCount());
                log.debug("executor.getQueue().size {}", executor.getQueue().size());
                log.debug("executor.isTerminating {}", executor.isTerminating());
                log.debug("executor.isShutdown {}", executor.isShutdown());
                log.debug("executor.isTerminated {}", executor.isTerminated());
                TimeUnit.SECONDS.sleep(sleepTimeSeconds);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting", e);
        }
    }
}

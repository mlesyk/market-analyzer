package org.mlesyk.orders.profitable.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.profitable.model.ProfitableOrders;
import org.mlesyk.orders.profitable.repository.ProfitableOrdersRepository;
import org.mlesyk.orders.profitable.repository.dto.ProfitableOrderBuyIdView;
import org.mlesyk.orders.profitable.repository.dto.ProfitableOrderSellIdView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class KafkaOrdersCleanerService {

    @Value("${app.orders.cleaner.registry_read_time_seconds}")
    Integer registryReadTimeSeconds;

    private final ProfitableOrdersRepository profitableOrdersRepository;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    public KafkaOrdersCleanerService(@Value("${app.orders.cleaner.cache_life_minutes}") Integer cacheLifeTime,
                                     ProfitableOrdersRepository profitableOrdersRepository,
                                     KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheLifeTime, TimeUnit.MINUTES) // Set a TTL of 120 minutes
                .build();
        this.profitableOrdersRepository = profitableOrdersRepository;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    private Set<Long> ordersRegistry = Collections.synchronizedSet(new HashSet<>());

    Cache<Long, Long> cache;

    static AtomicInteger counter = new AtomicInteger(0);

    @Value("${app.orders.cleaner.enabled}")
    Boolean cleanerEnabled;

    @KafkaListener(id = "handler_orders_cleaner_id", autoStartup = "false", topics = "handler_orders_registry",
            containerFactory = "ordersRegistryKafkaListenerContainerFactory",
            topicPartitions = {@TopicPartition(topic = "handler_orders_registry", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))})
    public void readAll(@Payload List<Long> ordersIdList) {
        int ordersCount = counter.addAndGet(ordersIdList.size());
        if(ordersCount > 10000) {
            log.debug("saved orders ID to cache: {}", ordersCount);
            log.debug("cache size: {}", ordersRegistry.size());
            counter.set(0);
        }
        ordersRegistry.addAll(ordersIdList);
    }

    @Scheduled(fixedDelayString = "${app.orders.cleaner.scheduler_delay}", initialDelayString = "${app.orders.cleaner.scheduler_initial_delay}")
    public void cleanerScheduler() {
        // fetch all orders in partition with retention time 30 min
        kafkaListenerEndpointRegistry.getListenerContainer("handler_orders_cleaner_id").start();
        try {
            TimeUnit.SECONDS.sleep(registryReadTimeSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage());
        }
        kafkaListenerEndpointRegistry.getListenerContainer("handler_orders_cleaner_id").stop();

        // get all orders id from database
        Collection<ProfitableOrderBuyIdView> buyOrderIdViewCollection = profitableOrdersRepository.findDistinctProfitableOrderBuyIdBy();
        Collection<ProfitableOrderSellIdView> sellOrderIdViewCollection = profitableOrdersRepository.findDistinctProfitableOrderSellIdBy();
        Set<Long> orders = buyOrderIdViewCollection.stream().map(ProfitableOrderBuyIdView::getBuyOrderIdPK).collect(Collectors.toSet());
        orders.addAll(sellOrderIdViewCollection.stream().map(ProfitableOrderSellIdView::getSellOrderIdPK).collect(Collectors.toSet()));
        log.info("buy/sell profitable orders ids from DB: {}", orders.size());
        log.info("orders registry from Kafka: {}", ordersRegistry.size());
        cache.putAll(ordersRegistry.stream().collect(Collectors.toMap(k -> k, v -> v)));
        ordersRegistry.clear();

        // find orders which does not exist anymore and remove them from database
        orders.removeAll(cache.asMap().keySet());
        log.info("orders which does not exist in orders registry: " + orders.size());

        if(Boolean.TRUE.equals(cleanerEnabled)) {
            if (!orders.isEmpty()) {
                Iterable<ProfitableOrders> allProfitableOrders = profitableOrdersRepository.findAll();
                log.info("profitable orders in DB before delete: {}", StreamSupport.stream(allProfitableOrders.spliterator(), false).count());

                List<ProfitableOrders> buyOrdersToDelete = profitableOrdersRepository.findByBuyOrderIdPKIn(orders);
                log.info("profitable buy orders to delete: {}", buyOrdersToDelete.size());
                profitableOrdersRepository.deleteAll(buyOrdersToDelete);
                List<ProfitableOrders> sellOrdersToDelete = profitableOrdersRepository.findBySellOrderIdPKIn(orders);
                log.info("profitable sell orders to delete: {}", sellOrdersToDelete.size());
                profitableOrdersRepository.deleteAll(sellOrdersToDelete);

                allProfitableOrders = profitableOrdersRepository.findAll();
                log.info("profitable orders in DB after delete {}", StreamSupport.stream(allProfitableOrders.spliterator(), false).count());
            }
        } else {
            log.info("Cleaner is disabled, orders are not deleting.");
        }
    }

}

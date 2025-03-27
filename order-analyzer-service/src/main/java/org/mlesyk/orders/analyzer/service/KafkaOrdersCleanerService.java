package org.mlesyk.orders.analyzer.service;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.analyzer.repository.RegionOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KafkaOrdersCleanerService {

    private final ReentrantReadWriteLock readWriteLock;
    private final RegionOrderRepository regionOrderRepository;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    public KafkaOrdersCleanerService(@Value("${app.orders.cleaner.cache_life_minutes}") Integer cacheLifeTime,
                                     ReentrantReadWriteLock readWriteLock,
                                     RegionOrderRepository regionOrderRepository,
                                     KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheLifeTime, TimeUnit.MINUTES) // Set a TTL of 120 minutes
                .build();
        this.readWriteLock = readWriteLock;
        this.regionOrderRepository = regionOrderRepository;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    @Value("${app.orders.cleaner.registry_read_time_seconds}")
    Integer registryReadTimeSeconds;

    @Value("${app.orders.cleaner.enabled}")
    Boolean cleanerEnabled;

    Cache<Long, Long> cache;

    private Set<Long> ordersRegistry = Collections.synchronizedSet(new HashSet<>());

    static AtomicInteger counter = new AtomicInteger(0);

    @KafkaListener(id = "storage_orders_cleaner_id", autoStartup = "false", topics = "storage_orders_registry",
            containerFactory = "ordersRegistryKafkaListenerContainerFactory",
            topicPartitions = {@TopicPartition(topic = "storage_orders_registry", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))})
    public void readAll(@Payload List<Long> ordersIdList) {
        int ordersCount = counter.addAndGet(ordersIdList.size());
        if (ordersCount > 10000) {
            log.debug("saved orders ID to cache: {}", ordersCount);
            log.debug("cache size: {}", ordersRegistry.size());
            counter.set(0);
        }
        ordersRegistry.addAll(ordersIdList);
    }

    @Scheduled(fixedDelayString = "${app.orders.cleaner.scheduler_delay}", initialDelayString = "${app.orders.cleaner.scheduler_initial_delay}")
    public void cleanerScheduler() {

        // fetch all orders in partition with retention time 30 min
        kafkaListenerEndpointRegistry.getListenerContainer("storage_orders_cleaner_id").start();
        try {
            TimeUnit.SECONDS.sleep(registryReadTimeSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage());
        }
        kafkaListenerEndpointRegistry.getListenerContainer("storage_orders_cleaner_id").stop();

        // get all orders id from database
        Set<Long> orders = new HashSet<>(regionOrderRepository.getAllOrdersIds());
        log.info("all orders from DB before cleaning: {}", orders.size());
        log.info("orders registry from Kafka: {}", ordersRegistry.size());
        cache.putAll(ordersRegistry.stream().collect(Collectors.toMap(k -> k, v -> v)));
        ordersRegistry.clear();
        // find orders which does not exist anymore and remove them from database
        orders.removeAll(cache.asMap().keySet());
        log.info("orders which does not exist in orders registry: {}", orders.size());

        if(Boolean.TRUE.equals(cleanerEnabled)) {
            readWriteLock.writeLock().lock();
            try {
                if (!orders.isEmpty()) {
                    if (orders.size() > 10_000) {
                        int batchesSize = 10_000;
                        Iterable<List<Long>> batchList = Iterables.partition(orders, batchesSize);
                        int batchNumber = 0;
                        for (List<Long> batch : batchList) {
                            log.info("Orders are deleting in batches, orders count: {} batch size: {} batch number: {}", orders.size(), batch.size(), batchNumber);
                            batchNumber++;
                            regionOrderRepository.deleteByOrderIdIn(new HashSet<>(batch));
                        }
                    } else {
                        log.info("Orders are deleting, orders count: {}", orders.size());
                        regionOrderRepository.deleteByOrderIdIn(orders);
                    }
                    Set<Long> allOrders = new HashSet<>(regionOrderRepository.getAllOrdersIds());
                    log.info("all orders from DB after cleaning: {}", allOrders.size());
                }
            } finally {
                readWriteLock.writeLock().unlock();
            }
        } else {
            log.info("Cleaner is disabled, orders are not deleting.");
        }
    }
}


package org.mlesyk.orders.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.analyzer.model.RegionOrder;
import org.mlesyk.orders.analyzer.repository.RegionOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Slf4j
public class KafkaOrdersConsumerService {

    private final ReentrantReadWriteLock readWriteLock;
    private final RegionOrderRepository regionOrderRepository;
    private final TaskExecutor taskExecutor;

    @Autowired
    public KafkaOrdersConsumerService(ReentrantReadWriteLock readWriteLock,
                                      RegionOrderRepository regionOrderRepository,
                                      @Qualifier("kafkaOrdersConsumerTaskExecutor") TaskExecutor taskExecutor) {
        this.readWriteLock = readWriteLock;
        this.regionOrderRepository = regionOrderRepository;
        this.taskExecutor = taskExecutor;
    }

    static AtomicInteger counter = new AtomicInteger(0);

    @KafkaListener(topics = "orders", containerFactory = "ordersKafkaListenerContainerFactory")
    public void receive(@Payload List<RegionOrder> orders) {
        readWriteLock.readLock().lock();
        try {
            int ordersCount = counter.addAndGet(orders.size());
            if (ordersCount > 1000) {
                log.info("saved orders to DB: {}", ordersCount);
                counter.set(0);
            }
            taskExecutor.execute(() -> regionOrderRepository.saveAll(orders));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}


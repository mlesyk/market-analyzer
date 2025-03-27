package org.mlesyk.orders.profitable.service;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.profitable.model.ProfitableOrders;
import org.mlesyk.orders.profitable.model.rest.ProfitableOrdersDTO;
import org.mlesyk.orders.profitable.repository.ProfitableOrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class KafkaProfitableOrdersConsumerService {

    private final ProfitableOrdersRepository profitableOrdersRepository;
    private final TaskExecutor taskExecutor;

    @Autowired
    public KafkaProfitableOrdersConsumerService(ProfitableOrdersRepository profitableOrdersRepository,
                                                @Qualifier("kafkaProfitableOrdersConsumerTaskExecutor") TaskExecutor taskExecutor) {
        this.profitableOrdersRepository = profitableOrdersRepository;
        this.taskExecutor = taskExecutor;
    }

    static AtomicInteger counter = new AtomicInteger(0);

    @KafkaListener(topics = "profitable_orders", containerFactory = "profitableOrdersKafkaListenerContainerFactory")
    public void receive(@Payload List<ProfitableOrdersDTO> orders) {
        int ordersCount = counter.addAndGet(orders.size());
        if(ordersCount > 10) {
            log.info("saved profitable orders to DB: {}", ordersCount);
            counter.set(0);
        }
        taskExecutor.execute(() -> {
            List<ProfitableOrders> profitableOrders = orders.stream().map(ProfitableOrders::new).toList();
            profitableOrdersRepository.saveAll(profitableOrders);
        });
    }
}

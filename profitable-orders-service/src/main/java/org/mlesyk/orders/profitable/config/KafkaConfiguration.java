package org.mlesyk.orders.profitable.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.mlesyk.orders.profitable.model.rest.ProfitableOrdersDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableScheduling
public class KafkaConfiguration {

    @Value("${app.kafka.hostname}")
    private String kafkaHost;

    @Value("${app.kafka.port}")
    private String kafkaPort;

    @Value("${app.kafka.topic.handler_orders_registry}")
    private String handlerOrdersRegistryTopicName;

    @Value("${app.kafka.retention_ms}")
    private String kafkaRetention;

    @Value("${app.kafka.topic.profitable_orders}")
    private String profitableOrdersTopicName;

    @Value(value = "${app.kafka.backoff.interval}")
    private Long interval;

    @Value(value = "${app.kafka.backoff.max_failure}")
    private Long maxAttempts;

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, Long>> ordersRegistryKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ordersRegistryConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, Long> ordersRegistryConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(ordersRegistryConsumerConfiguration());
    }

    @Bean
    public Map<String, Object> ordersRegistryConsumerConfiguration() {

        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "handlerOrderRegistryBatchReader");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "200");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return properties;
    }

    @Bean
    public NewTopic topicOrdersRegistry() {
        return TopicBuilder.name(handlerOrdersRegistryTopicName)
                .partitions(1)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, kafkaRetention)
                .build();
    }

    @Bean
    public NewTopic topicProfitableOrders() {
        return TopicBuilder.name(profitableOrdersTopicName)
                .partitions(1)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "600000")
                .build();
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        return new KafkaAdmin(configs);
    }

    @Bean
    public TaskExecutor kafkaProfitableOrdersConsumerTaskExecutor(@Value("${app.orders.consumer.kafka_orders_consumer_pool_size}") Integer poolSize,
                                                                  @Value("${app.orders.consumer.kafka_orders_consumer_queue_size}") Integer queueSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueSize);
        executor.setThreadNamePrefix("KafkaProfitableOrdersConsumerThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff = new FixedBackOff(interval, maxAttempts);
        return new DefaultErrorHandler((consumerRecord, exception) -> {
            throw new RuntimeException("Cannot save ProfitableOrders from Kafka topic to database, retry limit exceeded");
        }, fixedBackOff);
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, ProfitableOrdersDTO>> profitableOrdersKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, ProfitableOrdersDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setRecordMessageConverter(new StringJsonMessageConverter());
        factory.setConsumerFactory(ordersConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, ProfitableOrdersDTO> ordersConsumerFactory() {

        JsonDeserializer<ProfitableOrdersDTO> deserializer = new JsonDeserializer<>(ProfitableOrdersDTO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(consumerConfiguration(), new IntegerDeserializer(), deserializer);
    }

    @Bean
    public Map<String, Object> consumerConfiguration() {

        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "profitableOrderBatchReader");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "200");

        return properties;
    }
}
package org.mlesyk.orders.analyzer.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.mlesyk.orders.analyzer.model.RegionOrder;
import org.mlesyk.orders.analyzer.model.rest.ProfitableOrdersDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
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

    @Value("${app.kafka.topic.orders}")
    private String ordersTopicName;

    @Value("${app.kafka.topic.profitable_orders}")
    private String profitableOrdersTopicName;

    @Value("${app.kafka.topic.storage_orders_registry}")
    private String storageOrdersRegistryTopicName;

    @Value("${app.kafka.retention_ms}")
    private String kafkaRetention;

    @Value(value = "${app.kafka.backoff.interval}")
    private Long interval;

    @Value(value = "${app.kafka.backoff.max_failure}")
    private Long maxAttempts;

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, RegionOrder>> ordersKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, RegionOrder> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setRecordMessageConverter(new StringJsonMessageConverter());
        factory.setConsumerFactory(ordersConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff = new FixedBackOff(interval, maxAttempts);
        return new DefaultErrorHandler((consumerRecord, exception) -> {
            throw new RuntimeException("Cannot save Orders from Kafka topic to database, retry limit exceeded");
        }, fixedBackOff);
    }

    @Bean
    public ConsumerFactory<Long, RegionOrder> ordersConsumerFactory() {

        JsonDeserializer<RegionOrder> deserializer = new JsonDeserializer<>(RegionOrder.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(consumerConfiguration(), new LongDeserializer(), deserializer);
    }

    @Bean
    public Map<String, Object> consumerConfiguration() {

        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "regionOrderBatchReader");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "200");

        return properties;
    }

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
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "storageOrderRegistryBatchReader");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "200");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return properties;
    }

    @Bean
    public NewTopic topicOrders() {
        return TopicBuilder.name(ordersTopicName)
                .partitions(1)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, kafkaRetention)
                .build();
    }

    @Bean
    public NewTopic topicOrdersRegistry() {
        return TopicBuilder.name(storageOrdersRegistryTopicName)
                .partitions(1)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, kafkaRetention)
                .build();
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        return new KafkaAdmin(configs);
    }

    @Bean
    public TaskExecutor kafkaOrdersConsumerTaskExecutor(@Value("${app.orders.consumer.kafka_orders_consumer_pool_size}") Integer poolSize,
                                                        @Value("${app.orders.consumer.kafka_orders_consumer_queue_size}") Integer queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("KafkaOrdersConsumerThread-");
        executor.initialize();
        return executor;
    }

    // Integer is typeId as a key
    @Bean
    public ProducerFactory<Integer, ProfitableOrdersDTO> ordersProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<Integer, ProfitableOrdersDTO> ordersKafkaTemplate() {
        return new KafkaTemplate<>(ordersProducerFactory());
    }

    @Bean
    public NewTopic topicProfitableOrders() {
        return TopicBuilder.name(profitableOrdersTopicName)
                .partitions(1)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "600000")
                .build();
    }
}
package org.mlesyk.marketapi.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.mlesyk.marketapi.model.MarketOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
public class KafkaConfiguration {

    @Value("${app.kafka.topic.orders}")
    private String ordersTopicName;

    @Value("${app.kafka.topic.handler_orders_registry}")
    private String handlerOrdersRegistryTopicName;

    @Value("${app.kafka.topic.storage_orders_registry}")
    private String storageOrdersRegistryTopicName;

    @Value("${app.kafka.hostname}")
    private String kafkaHost;

    @Value("${app.kafka.port}")
    private String kafkaPort;

    @Value("${app.kafka.retention_ms}")
    private String kafkaRetention;

    @Bean
    public ProducerFactory<Long, MarketOrder> ordersProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<Long, MarketOrder> ordersKafkaTemplate() {
        return new KafkaTemplate<>(ordersProducerFactory());
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
    public ProducerFactory<Long, Long> ordersRegistryProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<Long, Long> ordersRegistryKafkaTemplate() {
        return new KafkaTemplate<>(ordersRegistryProducerFactory());
    }

    @Bean
    public NewTopic topicHandlerOrdersRegistry() {
        return TopicBuilder.name(handlerOrdersRegistryTopicName)
                .partitions(1)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, kafkaRetention)
                .build();
    }

    @Bean
    public NewTopic topicStorageOrdersRegistry() {
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
}
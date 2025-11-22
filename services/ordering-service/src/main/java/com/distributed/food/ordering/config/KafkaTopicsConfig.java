package com.distributed.food.ordering.config;

import com.distributed.food.ordering.messaging.OrderEventPayload;
import com.distributed.food.ordering.messaging.OrderStatusMessage;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic orderCreatedTopic(@Value("${app.kafka.topics.order-created:orders.created}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderStatusTopic(@Value("${app.kafka.topics.order-status:orders.status}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic kitchenStatusTopic(@Value("${app.kafka.topics.kitchen-status:restaurant.status}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public ProducerFactory<String, OrderEventPayload> orderEventProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties(null);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<String, OrderEventPayload> kafkaTemplate(
            ProducerFactory<String, OrderEventPayload> orderEventProducerFactory) {
        return new KafkaTemplate<>(orderEventProducerFactory);
    }

    @Bean
    public ConsumerFactory<String, OrderStatusMessage> orderStatusConsumerFactory(KafkaProperties properties) {
        JsonDeserializer<OrderStatusMessage> valueDeserializer = new JsonDeserializer<>(OrderStatusMessage.class);
        valueDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(
                properties.buildConsumerProperties(null),
                new StringDeserializer(),
                valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderStatusMessage> orderStatusListenerFactory(
            ConsumerFactory<String, OrderStatusMessage> orderStatusConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderStatusMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderStatusConsumerFactory);
        factory.setConcurrency(2);
        return factory;
    }
}

package com.distributed.food.restaurant.config;

import com.distributed.food.restaurant.messaging.KitchenStatusEvent;
import com.distributed.food.restaurant.messaging.OrderCreatedEvent;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public NewTopic orderCreatedTopic(@Value("${app.kafka.topics.order-created:orders.created}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic kitchenStatusTopic(@Value("${app.kafka.topics.kitchen-status:restaurant.status}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> orderCreatedConsumerFactory(KafkaProperties properties) {
        Map<String, Object> config = properties.buildConsumerProperties(null);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        JsonDeserializer<OrderCreatedEvent> valueDeserializer = new JsonDeserializer<>(OrderCreatedEvent.class);
        valueDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> orderCreatedListenerFactory(
            ConsumerFactory<String, OrderCreatedEvent> orderCreatedConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCreatedConsumerFactory);
        factory.setConcurrency(2);
        return factory;
    }

    @Bean
    public ProducerFactory<String, KitchenStatusEvent> kitchenStatusProducerFactory(KafkaProperties properties) {
        Map<String, Object> config = properties.buildProducerProperties(null);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, KitchenStatusEvent> kitchenStatusKafkaTemplate(
            ProducerFactory<String, KitchenStatusEvent> kitchenStatusProducerFactory) {
        return new KafkaTemplate<>(kitchenStatusProducerFactory);
    }
}

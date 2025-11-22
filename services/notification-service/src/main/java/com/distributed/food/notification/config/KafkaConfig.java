package com.distributed.food.notification.config;

import com.distributed.food.notification.messaging.OrderStatusEvent;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public NewTopic statusTopic(@Value("${app.kafka.topics.order-status:orders.status}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public ConsumerFactory<String, OrderStatusEvent> orderStatusConsumerFactory(KafkaProperties properties) {
        Map<String, Object> config = properties.buildConsumerProperties(null);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        JsonDeserializer<OrderStatusEvent> valueDeserializer = new JsonDeserializer<>(OrderStatusEvent.class);
        valueDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderStatusEvent> orderStatusListenerFactory(
            ConsumerFactory<String, OrderStatusEvent> orderStatusConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderStatusEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderStatusConsumerFactory);
        factory.setConcurrency(2);
        return factory;
    }
}

package com.distributed.food.ordering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class OrderingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderingServiceApplication.class, args);
    }
}

package com.andesstay.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String RESERVATIONS_EVENTS_TOPIC = "reservations.events";

    @Bean
    NewTopic reservationsEventsTopic() {
        return TopicBuilder.name(RESERVATIONS_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

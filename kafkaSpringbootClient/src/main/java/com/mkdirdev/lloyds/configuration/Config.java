package com.mkdirdev.lloyds.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import com.mkdirdev.lloyds.kafka.ApplicationRebalanceAware;

@EnableKafka
@Configuration
@EnableJpaRepositories
public class Config {

    @Value(value = "${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapAddress;
    @Autowired
    private DefaultKafkaConsumerFactory<String, String> consumerFactory;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory() {
        var concurrentConsumerFactory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        concurrentConsumerFactory.setConsumerFactory(consumerFactory);
        concurrentConsumerFactory.getContainerProperties()
                .setConsumerRebalanceListener(new ApplicationRebalanceAware());
        concurrentConsumerFactory.setBatchListener(true);
        return concurrentConsumerFactory;
    }

}

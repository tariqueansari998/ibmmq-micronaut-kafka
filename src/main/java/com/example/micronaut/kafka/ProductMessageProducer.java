package com.example.micronaut.kafka;

import com.example.micronaut.jms.parser.Product;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Bean;

@KafkaClient
public interface ProductMessageProducer {

    @Topic("pim_product_feed")
    void sendProduct(@KafkaKey String ean, Product product);
}

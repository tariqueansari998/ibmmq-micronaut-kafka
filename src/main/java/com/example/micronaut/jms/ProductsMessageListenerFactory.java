package com.example.micronaut.jms;

import com.example.micronaut.jms.config.MessageListenerFactory;
import com.example.micronaut.jms.config.MqProperties;


import io.micronaut.context.annotation.Bean;


@Bean
public class ProductsMessageListenerFactory implements MessageListenerFactory<ProductsMessageListener> {


    @Override
    public ProductsMessageListener build(String group, String tenant, MqProperties properties) {
        return new ProductsMessageListener(properties);
    }


}

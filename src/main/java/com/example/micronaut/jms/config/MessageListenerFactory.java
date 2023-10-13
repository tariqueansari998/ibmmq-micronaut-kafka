package com.example.micronaut.jms.config;


import jakarta.jms.MessageListener;

public interface MessageListenerFactory<M extends MessageListener> {

    M build(
        String group,
        String tenant,
        MqProperties properties
    );
}

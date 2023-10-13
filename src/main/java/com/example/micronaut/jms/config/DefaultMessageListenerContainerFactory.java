package com.example.micronaut.jms.config;


import io.micronaut.context.annotation.Bean;

import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;


@Bean
public class DefaultMessageListenerContainerFactory {

    Logger log = LoggerFactory.getLogger(DefaultMessageListenerContainerFactory.class);
    private final MqConnectionFactory mqConnectionFactory;


    @Inject
    public DefaultMessageListenerContainerFactory(MqConnectionFactory mqConnectionFactory) {

        this.mqConnectionFactory = mqConnectionFactory;
    }

    public DefaultMessageListenerContainer createMessageListenerContainer(
        MqProperties mqProperties,
        MessageListener messageListener
    ) {
        log.info("creating message listener");
        ConnectionFactory connectionFactory = mqConnectionFactory.createConnectionFactory(mqProperties);

        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(mqProperties.getQueueName());
        container.setConcurrency(mqProperties.getConcurrency());
        container.setAutoStartup(mqProperties.isAutoStartup());
        container.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        container.setSessionTransacted(true);
        container.setMessageListener(messageListener);
        container.setBeanName("DefaultMessageListenerContainer-" + mqProperties.getQueueName() + "-");
        log.info("Queue name:" + mqProperties.getQueueName());
        return container;
    }
}

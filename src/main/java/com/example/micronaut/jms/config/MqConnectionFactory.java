package com.example.micronaut.jms.config;

import com.example.micronaut.jms.ProductsMessageListener;

import com.ibm.mq.jakarta.jms.MQXAConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import io.micronaut.context.annotation.Bean;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Bean
public class MqConnectionFactory {

    private static Logger log = LoggerFactory.getLogger(MqConnectionFactory.class);

    @Data
    @Builder
    @EqualsAndHashCode
    private static class Key {

        private String queueManager;
        private String host;
        private int port;
        private String channel;
        private String username;
        private String password;
        private int clientReconnectTimeout;

        static Key from(MqProperties properties) {
            return Key.builder()
                .queueManager(properties.getQueueManager())
                .host(properties.getHost())
                .port(properties.getPort())
                .channel(properties.getChannel())
                .username(properties.getUsername())
                .password(properties.getPassword())
                //.clientReconnectTimeout(properties.getClientReconnectTimeout())
                .build();
        }
    }

    private Map<Key, ConnectionFactory> factories = new ConcurrentHashMap<>();

    public ConnectionFactory createConnectionFactory(MqProperties mqProperties) {
        log.info(mqProperties.toString());
        Key key = Key.from(mqProperties);
        return factories.computeIfAbsent(key, k -> {

            try {
                return tryCreateConnectionFactory(k);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private ConnectionFactory tryCreateConnectionFactory(Key key) throws JMSException {
        MQXAConnectionFactory factory = new MQXAConnectionFactory();
        factory.setQueueManager(key.getQueueManager());
        factory.setHostName(key.getHost());
        factory.setPort(key.getPort());
        factory.setChannel(key.getChannel());
        factory.setStringProperty(WMQConstants.USERID, key.getUsername());
        factory.setStringProperty(WMQConstants.PASSWORD, key.getPassword());
        factory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, false);
        factory.setClientReconnectTimeout(key.getClientReconnectTimeout());
        factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        factory.setClientReconnectOptions(WMQConstants.WMQ_CLIENT_RECONNECT);
        return (ConnectionFactory) factory;
    }
}
package com.example.micronaut.jms.config;

import io.micronaut.context.annotation.*;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import java.util.HashMap;
import java.util.Map;


@Factory
public class DefaultMQConfiguration<M extends MessageListener> {
    Logger log = LoggerFactory.getLogger(DefaultMQConfiguration.class);

    @Inject
    public DefaultMQConfiguration(DefaultMessageListenerContainerFactory messageListenerContainerFactory,MessageListenerFactory<M> listenerFactory, MapTransformer mapTransformer) {
        this.messageListenerContainerFactory = messageListenerContainerFactory;
        this.listenerFactory = listenerFactory;
        this.mapTransformer = mapTransformer;
    }


    @ConfigurationProperties("mq")
    public Map<String, Map<String, MqProperties>> queueConfiguration() {
        return new HashMap<>();
    }

    @Property(name = "mq")
    Map<String, Map<String, MqProperties>> queueConfigurationPro;

    @Inject
    private final DefaultMessageListenerContainerFactory messageListenerContainerFactory;

    @Inject
    private final MessageListenerFactory<M> listenerFactory;

    @Inject
    private final MapTransformer mapTransformer;


    @Context
    @Singleton
    public ContainerBean containers(
            Map<String, Map<String, MqProperties>> queueConfiguration
    ) {
       // log.info("creating containers", queueConfiguration);
        System.out.println("setting listener"+ queueConfiguration);
        log.info("queue config : "+queueConfigurationPro);
        Map<String, Map<String, DefaultMessageListenerContainer>> transformContainers = mapTransformer.transform(
                queueConfigurationPro,
                entry -> entry.getValue().isMessageConsumptionEnabled(),
                (group, tenant, properties) -> {
                    M listener = listenerFactory.build(group, tenant, properties);
                    return messageListenerContainerFactory.createMessageListenerContainer(properties, listener);
                }
        );
        ContainerBean bean = new ContainerBean();
        bean.setContainers(transformContainers);
        return bean;
    }



    @Context
    public Map<String, Map<String, M>> listeners(
            Map<String, Map<String, MqProperties>> queueConfiguration
    ) {
        return mapTransformer.transform(
                queueConfiguration,
                entry -> true,
                listenerFactory::build
        );
    }

}

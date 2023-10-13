package com.example.micronaut.jms.config;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.discovery.event.ServiceReadyEvent;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Bean
public class MqStarter implements ApplicationEventListener<ApplicationStartupEvent> {

    Logger log = LoggerFactory.getLogger(MqStarter.class);

    @Inject
    private ContainerBean containerBean;


    private void safeStart(DefaultMessageListenerContainer container) {
        log.info("Starting Message Queue to destination {}", container.getDestinationName());
        try{
            container.initialize();
            container.start();
        } catch (Exception exception) {
            log.error("Failed to initialise and start the queue", exception);
        }

    }


    @EventListener
    @Override
    public void onApplicationEvent(ApplicationStartupEvent event) {
        log.info("app event:" , event);
        Map<String, Map<String, DefaultMessageListenerContainer>> containers = containerBean.getContainers();
        if (containers.isEmpty()) {
            log.info("No Message Queue Containers found to start");
        } else {
            containers.values().forEach(m -> {
                m.values().forEach(container -> {
                    if (!container.isAutoStartup()) safeStart(container);
                });
            });
        }
    }


}

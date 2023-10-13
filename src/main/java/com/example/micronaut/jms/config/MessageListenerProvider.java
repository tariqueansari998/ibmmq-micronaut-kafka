package com.example.micronaut.jms.config;

import io.micronaut.context.annotation.Bean;
import jakarta.inject.Inject;

import javax.jms.MessageListener;
import java.util.Map;

@Bean
public class MessageListenerProvider<M extends MessageListener> {

    private Map<String, Map<String, M>> listeners;

    @Inject
    public MessageListenerProvider(Map<String, Map<String, M>> listeners) {
        this.listeners = listeners;
    }

    public M listenerFor(String group, String tenant) {
        Map<String, M> listeners = listenersFor(group);
        if (listeners.containsKey(tenant)) {
            return listeners.get(tenant);
        }
        throw new IllegalArgumentException(
            String.format("No MessageListener found matching group %s and tenant %s", group, tenant)
        );
    }

    public Map<String, M> listenersFor(String group) {
        if (listeners.containsKey(group)) {
            return listeners.get(group);
        }
        throw new IllegalArgumentException(
            String.format("No MessageListeners found matching group %s", group)
        );
    }
}

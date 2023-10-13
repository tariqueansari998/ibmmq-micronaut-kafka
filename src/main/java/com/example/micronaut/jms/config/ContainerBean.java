package com.example.micronaut.jms.config;

import lombok.Data;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.util.Map;

@Data
public class ContainerBean {

    Map<String, Map<String, DefaultMessageListenerContainer>> containers;
}

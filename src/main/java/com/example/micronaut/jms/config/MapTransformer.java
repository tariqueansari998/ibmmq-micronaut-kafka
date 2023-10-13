package com.example.micronaut.jms.config;

import io.micronaut.context.annotation.Bean;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Bean
public class MapTransformer {

    interface Mapper<T> {
        T map(String group, String tenant, MqProperties properties);
    }

    public <T> Map<String, Map<String, T>> transform(
        Map<String, Map<String, MqProperties>> source,
        Predicate<Map.Entry<String, MqProperties>> filter,
        Mapper<T> mapper
    ) {
        return source.entrySet().stream().collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().entrySet().stream().filter(filter).collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        e -> mapper.map(entry.getKey(), e.getKey(), e.getValue())
                    )
                )
            )
        );
    }
}

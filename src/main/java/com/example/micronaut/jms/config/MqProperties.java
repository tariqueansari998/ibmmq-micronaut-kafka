package com.example.micronaut.jms.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.serde.annotation.Serdeable;
import lombok.*;


@ToString(exclude = {"password"})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Serdeable
public class MqProperties {
    private String queueManager;
    private String host;
    private int port;
    private String channel;
    private String queueName;
    private String username;
    private String password;
    private boolean messageConsumptionEnabled;
    private int clientReconnectTimeout = 30;
    private String concurrency = "1";
    private boolean autoStartup = true;
    private boolean observeOnly = false;


}

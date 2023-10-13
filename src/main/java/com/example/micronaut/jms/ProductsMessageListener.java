package com.example.micronaut.jms;


import com.example.micronaut.jms.config.MqProperties;
import com.example.micronaut.jms.parser.XMLMessageParser;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.jms.JMSException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Pattern;

import static java.lang.String.format;

@Singleton
public class ProductsMessageListener implements MessageListener {

    private static Logger log = LoggerFactory.getLogger(ProductsMessageListener.class);

    private final Pattern entitiesPattern = Pattern.compile("(?s)<Entities(.*?)>");

    private final Pattern fulfilmentMessagePattern = Pattern.compile("<Tables>");

    private MqProperties properties;






    XMLMessageParser xmlMessageParser = new XMLMessageParser();



    public ProductsMessageListener(MqProperties properties) {
        this.properties = properties;
    }


    @Override
    public void onMessage(Message message) {
        String messageContent = null;
            try {
                validateMessage(message);
                messageContent = ((TextMessage) message).getText();
                log.info("message content:"+ messageContent);
                xmlMessageParser.parseXMLMessage(messageContent);
            } catch (Exception ex) {
                log.error("IMPORT_ERROR", ex);
            }
    }



    private void validateMessage(Message message) throws jakarta.jms.JMSException {
        if (!(message instanceof TextMessage) && message.getBody(String.class) == null) {
            log.error(format("Message has empty content, message id: %s", message.getJMSMessageID()));
        }
    }


}
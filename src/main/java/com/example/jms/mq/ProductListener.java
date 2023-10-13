package com.example.jms.mq;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.jms.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

public class ProductListener {

    private static final Logger log = LoggerFactory.getLogger(ProductListener.class);

    // System exit status value (assume unset value to be 1)
    private static int status = 1;

    // Create variables for the connection to MQ
    private static final String HOST = "localhost"; // Host name or IP address
    private static final int PORT = 1414; // Listener port for your queue manager
    private static final String CHANNEL = "DEV.APP.SVRCONN"; // Channel name
    private static final String QMGR = "QM1"; // Queue manager name
    private static final String APP_USER = "app"; // User name that application uses to connect to MQ
    private static final String APP_PASSWORD = "passw0rd"; // Password that the application uses to connect to MQ
    private static final String QUEUE_NAME = "DEV.QUEUE.1"; // Queue that the application uses to put and get messages to and from


    public static void main(String[] args) {
        JMSContext context;
        Destination destination;
        JMSConsumer consumer;
/*
        try {
            JmsFactoryFactory jmsFactoryFactory = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            JmsConnectionFactory jmsConnectionFactory = jmsFactoryFactory.createConnectionFactory();

            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
            jmsConnectionFactory.setIntProperty(WMQConstants.WMQ_PORT, PORT);
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
            jmsConnectionFactory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
            jmsConnectionFactory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "ProductListener (JMS)");
            jmsConnectionFactory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
            jmsConnectionFactory.setStringProperty(WMQConstants.USERID, APP_USER);
            jmsConnectionFactory.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);


            // Create JMS objects
            context = jmsConnectionFactory.createContext();
            destination = context.createQueue("queue:///" + QUEUE_NAME);

            consumer = context.createConsumer(destination); // autoclosable

            while(true){
                String receivedMessage = consumer.receiveBody(String.class, 1500000); // in ms or 15 seconds

                System.out.println("\nReceived message:\n" + receivedMessage);

                context.close();
                String kafkaMessage = parseXMLIntoKafkaMessage(receivedMessage);

            }



        } catch (JMSException e) {
            recordFailure(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JDOMException e) {
            throw new RuntimeException(e);
        }
*/


         while(true){

         }
    }

    private static String parseXMLIntoKafkaMessage(String mqMessage) throws ParserConfigurationException, IOException, JDOMException {

        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new InputSource(new StringReader(mqMessage)));
        log.info("Root element :" + document.getRootElement().getName());

        Element classElement = document.getRootElement();

        List<Element> entitiesList = classElement.getChildren();
        List<Element> entities = entitiesList.get(0).getChildren();
        System.out.println("----------------------------");

        for(Element entity : entities){
            String kafkaMessage = "{";
            System.out.println("entity : ----- " + entity);
            for(Element attributes : entity.getChildren()){
                System.out.println("Attribute : ----- ");
                for (Element attribute: attributes.getChildren()){
                    String attributeName = attribute.getAttributes().get(0).getValue();
                    Element content = (Element) attribute.getContent(1);
                    String value = ((Element)content.getContent(1)).getContent(0).getValue();
                    kafkaMessage = kafkaMessage + attributeName +":"+value +",";
                }
                kafkaMessage = kafkaMessage + "}";
            }
            System.out.println("kafka message: "+ kafkaMessage);
            writeToKafkaTopic(kafkaMessage);
        }

        return "";
    }

    private static void writeToKafkaTopic(String topicMessage){

        String bootStarpServer = "127.0.0.1:9092";

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", bootStarpServer);
        props.setProperty("key.serializer", StringSerializer.class.getName());
        props.setProperty("value.serializer", StringSerializer.class.getName());


        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("pim_product_feed", topicMessage);

        // send data
        producer.send(producerRecord,(metadata, exception) -> {
            if(exception == null){
                log.info("Message details :" + "\n" +"Partition " + metadata.partition() + "\n" + " Topic: "+ metadata.topic() + "\n" + " Offset: "+ metadata.offset());
            }else{
                log.error("Error while producing ", exception);
            }
        });
        // tell kafka to block until data is sent -- asynchronous
        producer.flush();

        //close producer
        producer.close();
    }

    private static void recordFailure(Exception ex) {
        if (ex != null) {
            if (ex instanceof JMSException) {
                processJMSException((JMSException) ex);
            } else {
                System.out.println(ex);
            }
        }
        System.out.println("FAILURE");
        status = -1;
        return;
    }

    /**
     * Process a JMSException and any associated inner exceptions.
     *
     * @param jmsex
     */
    private static void processJMSException(JMSException jmsex) {
        System.out.println(jmsex);
        Throwable innerException = jmsex.getLinkedException();
        if (innerException != null) {
            System.out.println("Inner exception(s):");
        }
        while (innerException != null) {
            System.out.println(innerException);
            innerException = innerException.getCause();
        }
        return;
    }

}

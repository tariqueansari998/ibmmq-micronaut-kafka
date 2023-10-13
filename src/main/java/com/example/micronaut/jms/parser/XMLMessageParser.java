package com.example.micronaut.jms.parser;

import com.example.micronaut.kafka.ProductMessageProducer;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Bean;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

@Singleton
@NoArgsConstructor
public class XMLMessageParser {

    private static Logger log = LoggerFactory.getLogger(XMLMessageParser.class);



    public void parseXMLMessage(String mqMessage) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new InputSource(new StringReader(mqMessage)));
        log.info("Root element :" + document.getRootElement().getName());

        Element classElement = document.getRootElement();

        List<Element> entitiesList = classElement.getChildren();
        List<Element> entities = entitiesList.get(0).getChildren();
        Product product;
        for(Element entity : entities){
            product = new Product();
            String kafkaMessage = "{";
            System.out.println("entity : ----- " + entity);
            for(Element attributes : entity.getChildren()){
                System.out.println("Attribute : ----- ");
                for (Element attribute: attributes.getChildren()){
                    String attributeName = attribute.getAttributes().get(0).getValue();

                    Element content = (Element) attribute.getContent(1);
                    String value = ((Element)content.getContent(1)).getContent(0).getValue();
                    if (attributeName.equalsIgnoreCase("ean")) {
                        product.setEan(value);
                    } else {
                        product.setArticleNumber(value);
                    }
                }
            }
            log.info("Product:" + product);
            ApplicationContext applicationContext = ApplicationContext.run();
            ProductMessageProducer producer = applicationContext.getBean(ProductMessageProducer.class);
            producer.sendProduct(product.getEan(),product);
        }
    }
}

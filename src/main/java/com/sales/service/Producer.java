package com.sales.service;

import com.google.common.io.Resources;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Kafka producer to generate various sales messages
 * Created by Prashant on 01-04-2017.
 */
public class Producer {

    public static void main(String [] args) throws IOException{
        KafkaProducer<String, String> producer;
        try (InputStream props = Resources.getResource("producer.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            producer = new KafkaProducer<>(properties);
        }

        try {
            for (int i = 1; i <= 100; i++) {

                producer.send(new ProducerRecord<String, String>(
                        "sale",
                        String.format("{\"productType\":\"Apple\", \"price\":\"0.20\"}")));


                if (i % 4 == 0) {
                    producer.send(new ProducerRecord<String, String>(
                            "multi-sale",
                            String.format("{\"productType\":\"Lemon\", \"quantity\":\"10\", \"price\":\"0.40\"}")));
                }

                if (i % 20 == 0) {
                    producer.send(new ProducerRecord<String, String>(
                            "adjustment",
                            String.format("{\"productType\":\"Apple\", \"operation\":\"ADD\", \"adjust\":\"0.40\"}")));
                }

                producer.flush();
                System.out.println("Sent msg number " + i);
            }
        } catch (Throwable throwable) {
            System.out.printf("%s", throwable.getStackTrace());
        } finally {
            producer.close();
        }
    }
}

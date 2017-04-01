package com.sales.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.sales.process.Processor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Kafka Consumer to listen to sales messages and processing them
 * Created by Prashant on 01-04-2017.
 */
public class Consumer {

    public static void main(String[] args) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        KafkaConsumer<String, String> consumer;
        Processor processor = new Processor();
        List<String> topicsToSubscribe = Arrays.asList("sale", "multi-sale", "adjustment");

        try (InputStream props = Resources.getResource("consumer.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            consumer = new KafkaConsumer<>(properties);
        }

        consumer.subscribe(topicsToSubscribe);

        int timeout = 100;
        int count = 0;

        while(true){

            ConsumerRecords<String, String> records = consumer.poll(timeout);

            for(ConsumerRecord<String, String> record : records){
                switch(record.topic()){
                    case "sale":
                        JsonNode sale = mapper.readTree(record.value());
                        processor.recordSale(sale.get("productType").asText(), new BigDecimal(sale.get("price").asDouble()).setScale(4, RoundingMode.HALF_UP));
                        count++;
                        break;
                    case "multi-sale":
                        JsonNode multiSale = mapper.readTree(record.value());
                        processor.recordMultiSale(multiSale.get("productType").asText(), multiSale.get("quantity").asInt(), new BigDecimal(multiSale.get("price").asDouble()).setScale(4, RoundingMode.HALF_UP));
                        count++;
                        break;
                    case "adjustment":
                        JsonNode adjustment = mapper.readTree(record.value());
                        processor.recordAdjustments(adjustment.get("productType").asText(), adjustment.get("operation").asText(), new BigDecimal(adjustment.get("adjust").asDouble()).setScale(4, RoundingMode.HALF_UP));
                        count++;
                        break;
                    default:
                        throw new IllegalStateException("Ignoring this invalid topic: " + record.topic());
                }
                /* After every 10th message received, log a report detailing the number of sales of each product and their total value*/
                if(count % 10 == 0){
                    System.out.println(processor.generateProductSummary());
                }
                /* After 50 messages, log a report of the adjustments that have been made to each sale type while the application was running.*/
                if(count % 50 == 0){
                    System.out.println(processor.generateAdjustmentSummary());
                }
            }
        }
    }
}
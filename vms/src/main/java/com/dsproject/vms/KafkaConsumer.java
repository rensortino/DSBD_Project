package com.dsproject.vms;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaConsumer {

    static org.apache.kafka.clients.consumer.KafkaConsumer consumer;

    public KafkaConsumer() {
        final Properties prop = Utils.loadProperties("kafka.properties");
        consumer = new org.apache.kafka.clients.consumer.KafkaConsumer(prop);
    }

    public void retrieveData (final String video_id) {
        List<String> topic = new ArrayList<String>();
        int counter = 0;
        topic.add("processed");
        try{
            consumer.subscribe(topic);
                ConsumerRecords records = consumer.poll(Duration.ofMillis(500));
            for (Object record: records) {
                System.out.println(record.toString());
                
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

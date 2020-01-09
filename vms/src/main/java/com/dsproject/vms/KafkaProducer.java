package com.dsproject.vms;

import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducer {
    static org.apache.kafka.clients.producer.KafkaProducer producer;

    public KafkaProducer() {
        final Properties prop = Utils.loadProperties("kafka.properties");
        producer = new org.apache.kafka.clients.producer.KafkaProducer(prop);
    }

    public void produceData (final String video_id) {
        try{
            producer.send(new ProducerRecord("process", video_id));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

package com.dsproject.vms.kafka;



import com.dsproject.vms.model.Video;
import com.dsproject.vms.model.VideoRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@KafkaListener(topics="${KAFKA_PROCESSED_TOPIC}")
@Transactional
public class UserListener {

    @Autowired
    VideoRepository repo;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${KAFKA_PROCESSED_TOPIC}")
    private  String kafka_processed_topic;

    @KafkaHandler
    public void listen(String message) {
        String[] message_parts = message.split("\\|");
        Optional<Video> video =  repo.findById(new ObjectId(message_parts[1]));
        if(message_parts[0].equals("processed")){

        	// The transaction executed successfully, update the video status
            video.get().setStatus("Available");
            repo.save(video.get());
        }
        else if(message_parts[0].equals("processingFailed")){

        	// Transaction failed, delete the directory which was supposed to 
        	// contain the processed video (named after the video id)
            video.get().setStatus("NotAvailable");
            repo.save(video.get());
            File dir = new File("/app/videos/" + message_parts[1]);

            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }


    }




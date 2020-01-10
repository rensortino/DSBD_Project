package com.dsproject.vms.services;
import com.dsproject.vms.model.*;
import com.dsproject.vms.exceptions.*;

import net.minidev.json.JSONObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Optional;

@Service
public class VideoService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${KAFKA_PROCESS_TOPIC}")
    private String kafkaProcessTopic;


    @Autowired
    VideoRepository videoRepo;

    @Autowired
    UserRepository userRepo;

    @Value(value = "${VIDEOPROCESSING_HOST}")
    private String videoProcessingHost;


    @KafkaListener(topics = "processed")
    public void listenKafka(String message) {
        System.out.println("Received Messasge in group foo: " + message);
    }

    @ResponseBody
    public Video insertVideo(@RequestBody VideoWrapper videowrapper) {
        if (videoRepo.findByName(videowrapper.getName()) != null) {
            throw new ExistingVideoNameException();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // se prendo il nome dalla richiesta un utente può pubblicare video con diversi nomi
        User author = userRepo.findByEmail(authentication.getName());
        Video video = new Video(videowrapper.getName(), author, videowrapper.getAuthor());
        return videoRepo.save(video);
    }

    @ResponseBody
    public Iterable<Video> getVideos() {
        return videoRepo.findAll();
    }

    public ResponseEntity getVideo(ObjectId id) {
        if (!videoRepo.findById(id).isPresent()) {
            throw new NotExistingVideoException();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/videofiles/" + id.toString() + "/video.mpd"));
        return new ResponseEntity(headers, HttpStatus.MOVED_PERMANENTLY);
        //return new RedirectView("file:///processedVideos/"+ id.toString()+ "video.mpd" );
    }

    @ResponseBody
    @Transactional
    public String uploadVideo(MultipartFile file, ObjectId videoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (file.isEmpty() || !videoRepo.existsById(videoId)) {
            throw new NoVideoFileException();
        }
        Optional<Video> video = videoRepo.findById(videoId);
        if (!video.get().getAuthor().getEmail().equals(authentication.getName())) {
            throw new NoUserMatchException();
        }
        RestTemplate VideoProcessingRequest = new RestTemplate();
        try {
            byte[] bytes = file.getBytes();
            // Creating the directory to store file
            File dir = new File("/app/videos/" + videoId);
            if (!dir.exists())
                dir.mkdirs();
            // Create the file on server
            File serverFile = new File(dir.getAbsolutePath() + File.separator + "video.mp4");
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
            System.out.println("Server File Location=" + serverFile.getAbsolutePath());
        } catch (Exception e) {
            throw new VideoFileException();
        }

        video.get().setStatus("Uploaded");
        this.kafkaTemplate.send(kafkaProcessTopic,"process|"+videoId.toString());
        videoRepo.save(video.get());
        return "Video Uploaded";

        // attendo la fine del processamento
        /*
        JSONObject VideoProcessingContent = new JSONObject();
        VideoProcessingContent.put("videoId", videoId.toString());
        ResponseEntity<JSONObject> VideoProcessingResult = VideoProcessingRequest.postForEntity(videoProcessingHost, VideoProcessingContent, JSONObject.class);
        System.out.println(VideoProcessingResult.getStatusCodeValue());
        if (VideoProcessingResult.getStatusCode().is2xxSuccessful()) {
            video.get().setStatus("Uploaded");
            return videoRepo.save(video.get());
        } else {
            throw new VideoProcessingException();
        }
         */
    }
}

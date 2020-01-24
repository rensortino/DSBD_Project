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

    @Value(value = "${GATEWAY_HOST}")
    private String apiGateway;


    @ResponseBody
    public Video insertVideo(@RequestBody VideoWrapper videowrapper) {
        // Inserts an entry in the database with the video id and the author
        if (videoRepo.findByName(videowrapper.getName()) != null) {
            throw new ExistingVideoNameException();
        }
        // Username taken from authentication context, otherwise any user can publish a video 
        // under a different username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User author = userRepo.findByEmail(authentication.getName());
        Video video = new Video(videowrapper.getName(), author, videowrapper.getAuthor());
        video.setStatus("WaitingUpload");
        return videoRepo.save(video);
    }

    @ResponseBody
    public Iterable<Video> getVideos() {
        return videoRepo.findAll();
    }

    public ResponseEntity getVideo(ObjectId id) {
        // Returns a 301 code redirecting to the file path of the processed video
        if (!videoRepo.findById(id).isPresent() || !videoRepo.findById(id).get().getStatus().equals("Available") ) {
            throw new NotExistingVideoException();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/videofiles/" + id.toString() + "/video.mpd"));
        return new ResponseEntity(headers, HttpStatus.MOVED_PERMANENTLY);
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
        if (!video.get().getStatus().equals("WaitingUpload")){
            throw new NoVideoStatusMatchException();
        }
        RestTemplate VideoProcessingRequest = new RestTemplate();
        try {
            // Takes a stream of bytes from the file
            byte[] bytes = file.getBytes();
            // Creates the directory where to store videos
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

        this.kafkaTemplate.send(kafkaProcessTopic,"process|"+videoId.toString());
        videoRepo.save(video.get());
        return "Video waiting for uploading";

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

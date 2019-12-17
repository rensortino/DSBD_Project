package com.dsproject.vms.controller;

import com.dsproject.vms.model.UserRepository;
import com.dsproject.vms.model.Video;
import com.dsproject.vms.model.VideoWrapper;
import com.dsproject.vms.services.VideoService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/videos")
public class VideoController {


  @Value(value = "${GATEWAY_HOST}")
  private String Gateway_host;

    @Autowired
    VideoService videoService;
    @Autowired
    UserRepository user;

    @PostMapping("/")
    @ResponseStatus(code = HttpStatus.CREATED,reason = "Video Created")
    @ResponseBody
    public Video insertVideo(@RequestBody VideoWrapper videoWrapper) {
      return videoService.insertVideo(videoWrapper);
    }

  @GetMapping("/")
  @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    Iterable<Video>  getVideos() {
      return videoService.getVideos();
    }

    @GetMapping("/{id}")
    ResponseEntity getVideo(@PathVariable ObjectId id) {
      return videoService.getVideo(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(code = HttpStatus.CREATED, reason = "Video Uploaded")
    @ResponseBody
    void uploadVideo(@RequestParam("file") MultipartFile file, @PathVariable("id") ObjectId id){
      videoService.uploadVideo(file, id);
      return;
    }

}

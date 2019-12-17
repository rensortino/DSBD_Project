package com.dsproject.vms.controller;

import com.dsproject.vms.model.*;
import net.minidev.json.JSONObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/videos")
public class VideoController {

  @Value(value = "${VIDEOPROCESSING_HOST}")
  private String Videoprocessing_host;

    @Autowired
    VideoRepository repo;
    @Autowired
    UserRepository user;

  @PostMapping("/")
  @ResponseStatus(code = HttpStatus.CREATED,reason = "Video Created")
    @ResponseBody
    Video insertVideo(@RequestBody VideoWrapper videowrapper){
      if (repo.findByName(videowrapper.getName()) != null){
          throw new ExistingVideoNameException();
      }
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // se prendo il nome dalla richiesta un utente può pubblicare video con diversi nomi
      User author = user.findByEmail(authentication.getName());
      Video video = new Video(videowrapper.getName(),author,videowrapper.getAuthor());
      return repo.save(video);
  }
  @ResponseStatus(code = HttpStatus.BAD_REQUEST,  reason = "Name of video already taken")
  class ExistingVideoNameException extends RuntimeException {
  }

  @GetMapping("/")
  @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    Iterable<Video>  getVideos() {
    return repo.findAll();}

    @GetMapping("/{id}")
    ResponseEntity getVideo(@PathVariable ObjectId id) {
      if(!repo.findById(id).isPresent()){
          throw new NotExistingVideoException();
      }
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(URI.create("/home/simoneonesta/DSBD_Project/storage/videofiles/video.mp4"));
      return new ResponseEntity(headers, HttpStatus.MOVED_PERMANENTLY);
      //return new RedirectView("file:///processedVideos/"+ id.toString()+ "video.mpd" );
  }

      @ResponseStatus(code = HttpStatus.NOT_FOUND,  reason = "video not found")
      class NotExistingVideoException extends RuntimeException {
      }

    @PostMapping("/{id}")
    @ResponseStatus(code = HttpStatus.CREATED, reason = "Video Uploaded")
    @ResponseBody
    void UploadVideo(@RequestParam("file") MultipartFile file, @PathVariable("id") ObjectId Video_id){
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (file.isEmpty() || !repo.existsById(Video_id)) {
        throw new NoVideoFileException();
      }
      Optional<Video> video = repo.findById(Video_id);
      if(!video.get().getAuthor().getEmail().equals(authentication.getName())){
          throw new NoUserMatchException();
      }
      RestTemplate VideoProcessingRequest = new RestTemplate();
        try {
          byte[] bytes = file.getBytes();
          // Creating the directory to store file
          File dir = new File("/videos/" + Video_id);
          if (!dir.exists())
            dir.mkdirs();
          // Create the file on server
          File serverFile = new File(dir.getAbsolutePath() + File.separator +  "video.mp4");
          BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
          stream.write(bytes);
          stream.close();
          System.out.println("Server File Location=" + serverFile.getAbsolutePath());
        } catch (Exception e) {
          throw new VideoFileException();
        }
        JSONObject VideoProcessingContent = new JSONObject();
        VideoProcessingContent.put("videoId",Video_id.toString());
        JSONObject VideoProcessingResult = VideoProcessingRequest.postForObject(Videoprocessing_host,VideoProcessingContent, JSONObject.class);
        if(VideoProcessingResult.getAsString("status").equals("ok")){
          video.get().setStatus("Uploaded");
          repo.save(video.get());
        }
        else{
          throw new VideoProcessingException();
        }

    }
  @ResponseStatus(code = HttpStatus.BAD_REQUEST,  reason = "No video found")
  class NoVideoFileException extends RuntimeException {
  }
  @ResponseStatus(code = HttpStatus.BAD_REQUEST,  reason = "You are not the video's owner")
  class NoUserMatchException extends RuntimeException {
  }
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR,  reason = "Error saving video")
  class VideoFileException extends RuntimeException {
  }
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR,  reason = "Error in processing the video")
  class VideoProcessingException extends RuntimeException {
  }
}

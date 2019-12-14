package com.dsproject.vms.controller;

import com.dsproject.vms.model.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    VideoRepository repo;
    @Autowired
    UserRepository user;

  @PostMapping("/")
    @ResponseBody
    Video insertVideo(@RequestBody VideoWrapper videowrapper){
      if (repo.findByName(videowrapper.getName()) != null){
          throw new ExistingVideoNameException();
      }
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // se prendo il nome dalla richiesta un utente può pubblicare video con diversi nomi
      User author = user.findByEmail(authentication.getName());
      Video video = new Video(videowrapper.getName(),author);
      return repo.save(video);
  }
  @ResponseStatus(code = HttpStatus.BAD_REQUEST,  reason = "Name of video already taken")
  class ExistingVideoNameException extends RuntimeException {
  }

  @GetMapping("/")
    @ResponseBody
    Iterable<Video>  getVideos() { return repo.findAll();}

    @GetMapping("/{id}")
    @ResponseBody
    String getVideo(@PathVariable ObjectId id) {
      if(!repo.findById(id).isPresent()){
          throw new NotExistingVideoException();
      }
      return "OK";
  }

    @ResponseStatus(code = HttpStatus.NOT_FOUND,  reason = "video not found")
    class NotExistingVideoException extends RuntimeException {
    }

    @PostMapping("/{id}")
    @ResponseBody
    String UploadVideo(@RequestParam("file") MultipartFile file, @PathVariable("id") ObjectId Video_id){
      if (file.isEmpty() || !repo.existsById(Video_id)) {
        throw new NoVideoFileException();
      }
        try {
          byte[] bytes = file.getBytes();
          // Creating the directory to store file
          String rootPath = "/home/Scrivania";
          File dir = new File("/home/simoneonesta/Scrivania/videos/" + Video_id);
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

        return "Video Uploaded";

    }
  @ResponseStatus(code = HttpStatus.BAD_REQUEST,  reason = "No video found")
  class NoVideoFileException extends RuntimeException {
  }
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR,  reason = "Error saving video")
  class VideoFileException extends RuntimeException {
  }
}

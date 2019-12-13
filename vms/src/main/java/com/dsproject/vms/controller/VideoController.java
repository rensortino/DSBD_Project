package com.dsproject.vms.controller;

import com.dsproject.vms.model.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
}

package com.dsproject.vms.controller;

import com.dsproject.vms.model.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class VideoController {

    @Autowired
    VideoRepository repo;


    public VideoController(VideoRepository repo) {
        this.repo = repo;
    }
}

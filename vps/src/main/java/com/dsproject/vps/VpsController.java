package com.dsproject.vps;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/videos")
public class VpsController {

    @PostMapping("/process")
    void processVideo(@RequestBody String Video_Folder){


    }


}

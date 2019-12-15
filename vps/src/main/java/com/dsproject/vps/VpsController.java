package com.dsproject.vps;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/videos")
public class VpsController {

    @PostMapping("/process")
    @ResponseBody
    JSONObject processVideo(@RequestBody VideoIdWrapper Video_Folder){

        Thread Script = new Thread(new Runnable() {
            @Override
            public void run() {
                ProcessBuilder Videoscript = new ProcessBuilder("./src/main/java/com/dsproject/vps/prova.sh",Video_Folder.getVideo_id()  );
                Process p = null;
                try {
                    p = Videoscript.start();
                    p.waitFor();
                } catch (Exception e) {
                    throw new VideoFileException();
                }

                    if(p.exitValue() != 0){
                        throw  new VideoFileException();
                    }


            }
        });
        try {
            Script.start();
            Script.join();
        } catch (Exception e) {
            throw  new VideoFileException();
        }

        JSONObject response = new JSONObject();
        response.put("status","Ok");
        return response;
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR,  reason = "Error in video's script")
    class VideoFileException extends RuntimeException {
    }


}

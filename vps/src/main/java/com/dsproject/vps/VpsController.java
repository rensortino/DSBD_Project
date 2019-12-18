package com.dsproject.vps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/videos")
public class VpsController {

    @Value(value = "${FLASK_HOST}")
    private String flaskHost;

    @PostMapping("/process")
    @ResponseBody
    public ResponseEntity processVideo(@RequestBody VideoIdWrapper videoId) {
        RestTemplate req = new RestTemplate();

        try {
            return req.postForObject(flaskHost, videoId, ResponseEntity.class);
        }
        catch(Exception e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    @ResponseBody
    JSONObject processVideo(@RequestBody VideoIdWrapper Video_Folder){

        Thread Script = new Thread(new Runnable() {
            @Override
            public void run() {
                ProcessBuilder Videoscript = new ProcessBuilder("/script/prova.sh",Video_Folder.getVideo_id()  );
                Process p;
                try {
                    p = Videoscript.start();
                    p.waitFor();
                    status = "COMPLETED";
                } catch (Exception e) {
                    status = "FAILED";
                }


            }
        });
        try {
            Script.start();
            Script.join();
        } catch (Exception e) {
            System.out.println(e);
            throw  new VideoFileException();
        }
        if(this.status.equals("FAILED")){
            System.out.println("errore esecuzione script");
            throw  new VideoFileException();
        }


        JSONObject response = new JSONObject();
        response.put("status","ok");
        return response;
    }
    */
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR,  reason = "Error in video's script")
    class VideoFileException extends RuntimeException {
    }


}

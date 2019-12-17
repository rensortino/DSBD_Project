package com.dsproject.vps;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/videos")
public class VpsController {
    public String Status;
    @PostMapping("/process")
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
                    Status = "COMPLETED";
                } catch (Exception e) {
                    Status = "FAILED";
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
        if(this.Status.equals("FAILED")){
            System.out.println("errore esecuzione script");
            throw  new VideoFileException();
        }


        JSONObject response = new JSONObject();
        response.put("status","ok");
        return response;
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR,  reason = "Error in video's script")
    class VideoFileException extends RuntimeException {
    }


}

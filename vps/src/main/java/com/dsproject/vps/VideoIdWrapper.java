package com.dsproject.vps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoIdWrapper {

    private String videoId;

    @JsonCreator
    VideoIdWrapper(@JsonProperty("videoId") String videoId) {
        this.videoId = videoId;
    }

    public String getVideo_id() {
        return videoId;
    }

    public void setVideo_id(String video_id) {
        videoId = video_id;
    }
}

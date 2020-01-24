package com.dsproject.vms.model;

// Support class to hold the data to be stored in the database
public class VideoWrapper {

    private String name;
    private String author_name;

    public VideoWrapper(String name, String author) {
        this.name = name;
        this.author_name = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author_name;
    }

    public void setAuthor(String author) {
        this.author_name = author;
    }
}

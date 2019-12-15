package com.dsproject.vms.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection="videos")
public class Video {

    @Id
    private ObjectId _id;
    @NotNull
    @Indexed(unique=true)
    private String name;
    @NotNull
    private String author_name;
    @DBRef
    private User author;

    private String status;

    @JsonCreator
    public Video(String name, User author,String author_name) {
        this.name = name;
        this.author_name = author_name;
        this.author = author;
    }


    @JsonGetter("_id")
    public String get_id_string() {
        return _id.toHexString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
        this.author_name = author.getName();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}

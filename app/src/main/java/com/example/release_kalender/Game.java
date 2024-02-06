package com.example.release_kalender;

import java.io.Serializable;

public class Game implements Serializable {
    private String id, name, publisher, releaseDate, description, imageURL, genre;
    private int likeCount;

    public Game(){

    }

    public Game(String id, String name, String publisher, String releaseDate, String description, String imageURL, String genre) {
        this.id = id;
        this.name = name;
        this.publisher = publisher;
        this.releaseDate = releaseDate;
        this.description = description;
        this.imageURL = imageURL;
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}

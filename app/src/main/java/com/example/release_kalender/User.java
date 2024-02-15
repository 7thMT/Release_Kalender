package com.example.release_kalender;

import java.util.List;

public class User {

    private String name, username, email, userId;
    private List<String> savedGames;
    private List<String> likedGames;

    public User(String name, String username, String email, String userId) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    public User(){

    }

    public String getName() {
        return name;
    }

}



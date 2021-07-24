package com.example.chattingapp.Model;

public class User {
    String id;
    String username;
    String imageUrl;

    public User() {

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

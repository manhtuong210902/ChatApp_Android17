package com.example.chatapp;

public class User {
    private String name;
    private int image;
    private boolean isOnline;
    private String time;
    private String id;

    public String getName() {
        return name;
    }

    public User(String id,String name, int image, boolean isOnline, String time) {
        this.name = name;
        this.image = image;
        this.isOnline = isOnline;
        this.time = time;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

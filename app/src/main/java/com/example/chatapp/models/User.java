package com.example.chatapp.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String name;
    private String image;
    private boolean isOnline;

    public User(){

    }

    public User(String uid, String name, String image, boolean isOnline) {
        this.uid = uid;
        this.name = name;
        this.image = image;
        this.isOnline = isOnline;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("image", image);
        result.put("isOnline", isOnline);
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}

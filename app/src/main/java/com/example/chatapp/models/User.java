package com.example.chatapp.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String name;
    private String email;
    private String image;
    public boolean isOnline;
    private String did;

    public User(){};

    public User(String uid, String email, String name, String image, boolean isOnline, String did) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.image = image;
        this.isOnline = isOnline;
        this.did = did;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("email", email);
        result.put("name", name);
        result.put("image", image);
        result.put("isOnline", isOnline);
        result.put("did", did);
        return result;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
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
        return this.isOnline;
    }

    public boolean getIsOnline() {
        return this.isOnline;
    }

    public void setIsOnline(boolean online) {
        isOnline = online;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }
}

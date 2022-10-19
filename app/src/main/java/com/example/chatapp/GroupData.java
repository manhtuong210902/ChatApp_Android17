package com.example.chatapp;

public class GroupData {
    private String id;
    private String name;
    private int image;
    private String status;

    public GroupData(String id,String name, int image, String status) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

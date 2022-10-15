package com.example.chatapp;

public class ChatUser {
    private String name;
    private int image;
    private String message;
    private String time;
    private String numMessage;

    public ChatUser(String name, int image, String message, String time, String numMessage) {
        this.name = name;
        this.image = image;
        this.message = message;
        this.time = time;
        this.numMessage = numMessage;
    }

    public String getName() {
        return name;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumMessage() {
        return numMessage;
    }

    public void setNumMessage(String numMessage) {
        this.numMessage = numMessage;
    }

    @Override
    public String toString() {
        return "ChatUser{" +
                "name='" + name + '\'' +
                ", image=" + image +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", numMessage='" + numMessage + '\'' +
                '}';
    }
}

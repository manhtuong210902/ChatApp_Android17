package com.example.chatapp.models;

public class CallHistory {
    private String name;
    private int image;
    private boolean isMissed;
    private boolean isVideoCall;
    private boolean isOnline;
    private String time;
    private int id;

    public CallHistory(int id,String name, int image, boolean isMissed, boolean isVideoCall, boolean isOnline, String time) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isMissed = isMissed;
        this.isVideoCall = isVideoCall;
        this.time = time;
        this.isOnline = isOnline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
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

    public String getToStringIsMissed() {
        return this.isMissed ? "Cuộc gọi nhỡ" : "Cuộc gọi đến";
    }

    public boolean isMissed() {
        return isMissed;
    }

    public void setMissed(boolean missed) {
        isMissed = missed;
    }

    public boolean isVideoCall() {
        return isVideoCall;
    }

    public void setVideoCall(boolean videoCall) {
        isVideoCall = videoCall;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

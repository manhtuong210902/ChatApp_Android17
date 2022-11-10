package com.example.chatapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {
    private String gid;
    private String name;
    private ArrayList<String> listUidMember;
    private String imageId;
    private boolean isOnline;
    private String lastMessage;

    public Group() {
    }

    public Group(String gid, String name, ArrayList<String> listUidMember, String imageId, boolean isOnline, String lastMessage) {
        this.gid = gid;
        this.name = name;
        this.listUidMember = listUidMember;
        this.imageId = imageId;
        this.isOnline = isOnline;
        this.lastMessage = lastMessage;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("gid", gid);
        result.put("name", name);
        result.put("listUidMember", listUidMember);
        result.put("imageId", imageId);
        result.put("isOnline", isOnline);
        result.put("lastMessage", lastMessage);
        return result;
    }

    @Override
    public String toString() {
        return "Group{" +
                "gid='" + gid + '\'' +
                ", name='" + name + '\'' +
                ", listUidMember=" + listUidMember +
                ", imageId='" + imageId + '\'' +
                ", isOnline=" + isOnline +
                ", lastMessage='" + lastMessage + '\'' +
                '}';
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getListUidMember() {
        return listUidMember;
    }

    public void setListUidMember(ArrayList<String> listUidMember) {
        this.listUidMember = listUidMember;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}

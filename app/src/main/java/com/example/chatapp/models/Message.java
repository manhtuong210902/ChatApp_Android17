package com.example.chatapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Message {
    private String mid;
    private String uid;
    private String gid;
    private ArrayList<String> listMemberSeen;
    private boolean isImage;
    private String imageId;

    public Message(String mid, String uid, String gid, ArrayList<String> listMemberSeen, boolean isImage, String imageId) {
        this.mid = mid;
        this.uid = uid;
        this.gid = gid;
        this.listMemberSeen = listMemberSeen;
        this.isImage = isImage;
        this.imageId = imageId;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mid", mid);
        result.put("uid", uid);
        result.put("gid", gid);
        result.put("listMemberSeen", listMemberSeen);
        result.put("isImage", isImage);
        result.put("imageId", imageId);
        return result;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public ArrayList<String> getListMemberSeen() {
        return listMemberSeen;
    }

    public void setListMemberSeen(ArrayList<String> listMemberSeen) {
        this.listMemberSeen = listMemberSeen;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}

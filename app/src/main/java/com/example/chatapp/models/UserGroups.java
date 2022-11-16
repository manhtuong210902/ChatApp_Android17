package com.example.chatapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserGroups {
    private String uid;
    private ArrayList<String> listGid;

    public UserGroups(String uid, ArrayList<String> listGid) {
        this.uid = uid;
        this.listGid = listGid;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("listGid", listGid);
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getListGid() {
        return listGid;
    }

    public void setListGid(ArrayList<String> listGid) {
        this.listGid = listGid;
    }

}

package com.example.chatapp;

import java.util.HashMap;

public class ExpPersonalListData {
    public  static HashMap<String,HashMap<String,String>> getData(){
        HashMap<String,HashMap<String,String>> list=new HashMap<>();
        HashMap<String,String> per=new HashMap<>();
        per.put("Name","bay bi");
        per.put("Email","@.com");
        per.put("Time","0 a.m");
        per.put("Location","aaa");
        list.put("Personal info",per);
        return list;
    }
}

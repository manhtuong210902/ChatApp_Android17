package com.example.chatapp.others;

import java.util.HashMap;

public class ExpPersonalListData {
    public  static HashMap<String,HashMap<String,String>> getData(){
        HashMap<String,HashMap<String,String>> list=new HashMap<>();
        HashMap<String,String> per=new HashMap<>();
        per.put("Name","Mạnh Tường");
        per.put("Email","mtuong669@gmail.com");
        list.put("Personal info",per);
        return list;
    }
}

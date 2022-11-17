package com.example.chatapp.others;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ExpandableListDataPumb {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        List<String> options = new ArrayList<String>();
        options.add("Dark mode");
        options.add("Show notification");
        options.add("Status");
        expandableListDetail.put("Options", options);
        return expandableListDetail;
    }
}

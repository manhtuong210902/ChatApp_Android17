package com.example.chatapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPumb {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();



        List<String> privacy = new ArrayList<String>();
        privacy.add("Profile photo");
        privacy.add("Last seen");
        privacy.add("Status");
        privacy.add("Read receipts");
        privacy.add("Groups");

        List<String>security = new ArrayList<String>();
        security.add("Show security notification");
        security.add("Logout");

        List<String> help=new ArrayList<String>();
        help.add("FAQs");
        help.add("Contacts");
        help.add("Terms&Privacy policy");

        expandableListDetail.put("Privacy", privacy);
        expandableListDetail.put("Help", help);
        expandableListDetail.put("Security",security);
        return expandableListDetail;
    }
}

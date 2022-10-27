package com.example.chatapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;


import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {
    LinearLayout llProfile;


    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llProfile = (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);


        expandableListView = (ExpandableListView) llProfile.findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPumb.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(),expandableListTitle,expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        return llProfile;
    }
}

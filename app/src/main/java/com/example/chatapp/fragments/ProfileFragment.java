package com.example.chatapp.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;


import androidx.fragment.app.Fragment;

import com.example.chatapp.R;
import com.example.chatapp.activities.LoginActivity;
import com.example.chatapp.adapters.CustomExpPersonalListAdapter;
import com.example.chatapp.adapters.CustomExpandableListAdapter;
import com.example.chatapp.others.ExpPersonalListData;
import com.example.chatapp.others.ExpandableListDataPumb;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {
    LinearLayout llProfile;


    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    ExpandableListView personalListView;
    ExpandableListAdapter personalListAdapter;
    List<String> personalListTitle;
    HashMap<String,HashMap<String,String>> personalListDetail;
    Button btn_logout;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llProfile = (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);

        personalListView=(ExpandableListView) llProfile.findViewById(R.id.expandablePersonalListView);
        personalListDetail= ExpPersonalListData.getData();
        personalListTitle=new ArrayList<String>(personalListDetail.keySet());
        personalListAdapter=new CustomExpPersonalListAdapter(getContext(),personalListTitle,personalListDetail);
        personalListView.setAdapter(personalListAdapter);

        expandableListView = (ExpandableListView) llProfile.findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPumb.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(),expandableListTitle,expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();
        btn_logout = llProfile.findViewById(R.id.btn_logout);
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("EmailLogin", "");
                editor.putString("PasswordLogin", "");
                editor.commit();
                mAuth.signOut();
                startActivity(intent);
            }
        });
        return llProfile;
    }
}

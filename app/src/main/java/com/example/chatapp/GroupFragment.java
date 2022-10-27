package com.example.chatapp;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {
    private LinearLayout llGroups;
    private RecyclerView recyclerView_GroupList;
    private List<GroupData> GroupsData = new ArrayList<>();
    private GroupsAdapter groupsAdapter;
    private ImageView imageView_btnGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llGroups = (LinearLayout) inflater.inflate(R.layout.fragment_group, container, false);
        imageView_btnGroup= llGroups.findViewById(R.id.imageView_btnGroup);

//        recyclerView_GroupList = findViewById(R.id.recyclerView_GroupList);
        GroupsData.add( new GroupData("1","#General1",R.drawable.cute1,"20+"));
        GroupsData.add( new GroupData("2","#General2",R.drawable.cute2,"20+"));
        GroupsData.add( new GroupData("3","#General3",R.drawable.cute3,"new"));
        GroupsData.add( new GroupData("4","#General4",R.drawable.cute1,"20+"));
        GroupsData.add( new GroupData("5","#General5",R.drawable.cute2,"20+"));
        GroupsData.add( new GroupData("6","#General6",R.drawable.cute3,"20+"));
        GroupsData.add( new GroupData("7","#General7",R.drawable.cute1,"20+"));
        GroupsData.add( new GroupData("8","#General8",R.drawable.cute2,"20+"));
        GroupsData.add( new GroupData("9","#General9",R.drawable.cute3,"20+"));
        GroupsData.add( new GroupData("10","#General10",R.drawable.cute1,"20+"));

        groupsAdapter = new GroupsAdapter(getContext(), GroupsData);
        recyclerView_GroupList = llGroups.findViewById(R.id.recyclerView_GroupList);
        recyclerView_GroupList.setHasFixedSize(true);
        recyclerView_GroupList.setLayoutManager(new GridLayoutManager(getContext(),1));
        groupsAdapter = new GroupsAdapter(getContext(), GroupsData);
        recyclerView_GroupList.setAdapter(groupsAdapter);

        imageView_btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddGroupActivity.class);
                startActivity(intent);
            }
        });
        return llGroups;
    }

}
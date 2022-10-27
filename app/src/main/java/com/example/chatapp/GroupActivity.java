package com.example.chatapp;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends Activity {
    private RecyclerView recyclerView_GroupList;
    private List<GroupData> GroupsData = new ArrayList<>();
    private GroupsAdapter groupsAdapter;
    private ImageView imageView_btnGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        imageView_btnGroup= findViewById(R.id.imageView_btnGroup);

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

        groupsAdapter = new GroupsAdapter(GroupActivity.this, GroupsData);
        recyclerView_GroupList = findViewById(R.id.recyclerView_GroupList);
        recyclerView_GroupList.setHasFixedSize(true);
        recyclerView_GroupList.setLayoutManager(new GridLayoutManager(GroupActivity.this,1));
        groupsAdapter = new GroupsAdapter(GroupActivity.this, GroupsData);
        recyclerView_GroupList.setAdapter(groupsAdapter);

        imageView_btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, AddGroupActivity.class);
                startActivity(intent);
            }
        });

    }

}
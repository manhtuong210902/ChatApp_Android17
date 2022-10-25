package com.example.chatapp;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends Activity {
    private EditText editText_groupName;
    private RecyclerView recyclerView_listFriend;
    private List<CallHistory> listFriend;
    private AddGroupAdapter addGroupAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        listFriend = new ArrayList<>();
        editText_groupName = findViewById(R.id.editText_groupName);
        recyclerView_listFriend = findViewById(R.id.recyclerView_listFriend);
        Data();

        recyclerView_listFriend.setHasFixedSize(true);
        recyclerView_listFriend.setLayoutManager(new GridLayoutManager(AddGroupActivity.this,1));
        addGroupAdapter = new AddGroupAdapter(AddGroupActivity.this, listFriend);
        recyclerView_listFriend.setAdapter(addGroupAdapter);
    }

    private void Data() {
        listFriend.add(new CallHistory("Mew1", R.drawable.cute1, false, false, true, "22:20"));
        listFriend.add(new CallHistory("Mew2", R.drawable.cute2, true, false, true, "22:21"));
        listFriend.add(new CallHistory("Mew3", R.drawable.cute3, false, true, true, "22:22"));
        listFriend.add(new CallHistory("Mew4", R.drawable.cute1, false, false, true, "22:23"));
        listFriend.add(new CallHistory("Mew5", R.drawable.cute2, true, true, true, "22:24"));
        listFriend.add(new CallHistory("Mew6", R.drawable.cute3, false, true, true, "22:25"));
        listFriend.add(new CallHistory("Mew7", R.drawable.cute1, false, false, true, "22:26"));
        listFriend.add(new CallHistory("Mew1", R.drawable.cute2, true, true, true, "22:27"));
        listFriend.add(new CallHistory("Mew1", R.drawable.cute3, false, false, true, "22:28"));
        listFriend.add(new CallHistory("Mew1", R.drawable.cute1, true, true, true, "22:29"));
        listFriend.add(new CallHistory("Mew1", R.drawable.cute2, true, false, true, "22:30"));
        listFriend.add(new CallHistory("Mew1", R.drawable.cute3, false, true, true, "22:31"));

    }
}
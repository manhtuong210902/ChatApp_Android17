package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends Activity {
    private EditText editText_groupName;
    private RecyclerView recyclerView_listFriend, recyclerView_listSelected;
    private List<CallHistory> listFriend, selectedList;
    private AddGroupAdapter addGroupAdapter;
    private SelectedGroupAdapter selectedGroupAdapter;
    LinearLayout layout_selectedMember, linearLayout;
    ImageView btn_close;
    TextView textView_Title, textView_numSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        listFriend = new ArrayList<>();
        editText_groupName = findViewById(R.id.editText_groupName);
        recyclerView_listFriend = findViewById(R.id.recyclerView_listFriend);
        recyclerView_listSelected = findViewById(R.id.recyclerView_listSelected);
        layout_selectedMember = findViewById(R.id.layout_selectedMember);
        linearLayout = findViewById(R.id.linearLayout);
        textView_numSelected = findViewById(R.id.textView_numSelected);
        textView_Title = findViewById(R.id.textView_Title);
        Data();
        selectedList = new ArrayList<>();

        recyclerView_listFriend.setHasFixedSize(true);
        recyclerView_listFriend.setLayoutManager(new GridLayoutManager(AddGroupActivity.this,1));
        addGroupAdapter = new AddGroupAdapter(AddGroupActivity.this, listFriend, addSelectedListListener);
        recyclerView_listFriend.setAdapter(addGroupAdapter);

        layout_selectedMember.setVisibility(View.INVISIBLE);
        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    private final AddSelectedListListener addSelectedListListener = new AddSelectedListListener() {
        @Override
        public void onMemberClicked(int pos) {
            selectedList.add(listFriend.get(pos));
            if (selectedList.size() != 0 ){
                layout_selectedMember.setVisibility(View.VISIBLE);
            }
            textView_numSelected.setText("Selected: "+ String.valueOf(selectedList.size()));
            recyclerView_listSelected.setHasFixedSize(true);
            recyclerView_listSelected.setLayoutManager(new LinearLayoutManager(AddGroupActivity.this, LinearLayoutManager.HORIZONTAL, false));
            selectedGroupAdapter = new SelectedGroupAdapter(AddGroupActivity.this, selectedList, removeSelectedListener);
            recyclerView_listSelected.setAdapter(selectedGroupAdapter);

            recyclerView_listFriend.setHasFixedSize(true);
            recyclerView_listFriend.setLayoutManager(new GridLayoutManager(AddGroupActivity.this,1));
            addGroupAdapter = new AddGroupAdapter(AddGroupActivity.this, listFriend, addSelectedListListener);
            recyclerView_listFriend.setAdapter(addGroupAdapter);
        }
    };

    private final RemoveSelectedListener removeSelectedListener = new RemoveSelectedListener() {
        @Override
        public void onSelectedClicked(int pos) {
            selectedList.remove(pos);
            if (selectedList.size() == 0 ){
                layout_selectedMember.setVisibility(View.INVISIBLE);
            }
            textView_numSelected.setText("Selected: "+ String.valueOf(selectedList.size()));
            recyclerView_listSelected.setHasFixedSize(true);
            recyclerView_listSelected.setLayoutManager(new LinearLayoutManager(AddGroupActivity.this, LinearLayoutManager.HORIZONTAL, false));
            selectedGroupAdapter = new SelectedGroupAdapter(AddGroupActivity.this, selectedList, removeSelectedListener);
            recyclerView_listSelected.setAdapter(selectedGroupAdapter);

            recyclerView_listFriend.setHasFixedSize(true);
            recyclerView_listFriend.setLayoutManager(new GridLayoutManager(AddGroupActivity.this,1));
            addGroupAdapter = new AddGroupAdapter(AddGroupActivity.this, listFriend, addSelectedListListener);
            recyclerView_listFriend.setAdapter(addGroupAdapter);
        }
    };
}

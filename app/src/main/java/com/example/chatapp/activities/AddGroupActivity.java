package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.adapters.ChatMessageAdapter;
import com.example.chatapp.db.DbReference;
import com.example.chatapp.models.AddGroupUser;
import com.example.chatapp.interfaces.AddSelectedListListener;
import com.example.chatapp.R;
import com.example.chatapp.adapters.SelectedGroupAdapter;
import com.example.chatapp.adapters.AddGroupAdapter;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.models.Group;
import com.example.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddGroupActivity extends AppCompatActivity {
    private EditText editText_groupName;
    private RecyclerView recyclerView_listFriend, recyclerView_listSelected;
    private List<AddGroupUser> listFriend, listSelected;
    private ArrayList<String> listUID;
    DatabaseReference databaseReference;

    private AddGroupAdapter addGroupAdapter;
    private SelectedGroupAdapter selectedGroupAdapter;
    LinearLayout layout_selectedMember, linearLayout;
    ImageView btn_close;
    TextView textView_Title, textView_numSelected;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    ImageView imageView_btnGroup;
    SearchView searchView_SearchUser;
    private DbReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().hide();

        listFriend = new ArrayList<>();
        listSelected = new ArrayList<>();
        mDatabase = DbReference.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        editText_groupName = findViewById(R.id.editText_groupName);
        recyclerView_listFriend = findViewById(R.id.recyclerView_listFriend);
        recyclerView_listSelected = findViewById(R.id.recyclerView_listSelected);
        layout_selectedMember = findViewById(R.id.layout_selectedMember);
        linearLayout = findViewById(R.id.linearLayout);
        textView_numSelected = findViewById(R.id.textView_numSelected);
        textView_Title = findViewById(R.id.textView_Title);
        imageView_btnGroup = findViewById(R.id.imageView_btnGroup);
        String currID = mAuth.getCurrentUser().getUid();

        recyclerView_listFriend.setLayoutManager(new GridLayoutManager(AddGroupActivity.this, 1));
        addGroupAdapter = new AddGroupAdapter(AddGroupActivity.this, listFriend, addSelectedListListener);
        recyclerView_listFriend.setAdapter(addGroupAdapter);
        searchFullUser();

        //search user
        searchView_SearchUser = findViewById(R.id.searchView_SearchUser);
        searchView_SearchUser.clearFocus();
        searchView_SearchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUserList(newText);
                return false;
            }
        });

        //end search user

        recyclerView_listSelected.setLayoutManager(new LinearLayoutManager(AddGroupActivity.this, LinearLayoutManager.HORIZONTAL, false));
        selectedGroupAdapter = new SelectedGroupAdapter(AddGroupActivity.this, listSelected, removeSelectedListener);
        recyclerView_listSelected.setAdapter(selectedGroupAdapter);

        layout_selectedMember.setVisibility(View.INVISIBLE);
        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        db = new DbReference();
        imageView_btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = editText_groupName.getText().toString();
                if(groupName.isEmpty()){
                    Toast.makeText(AddGroupActivity.this,"Group name invalid!!", Toast.LENGTH_LONG).show();
                    return;
                }
                ArrayList<String> listUidMember = new ArrayList<>();
                listSelected.forEach((item) -> {
                    listUidMember.add(item.getInfo().getUid());
                });
                listUidMember.add(mAuth.getCurrentUser().getUid().toString());
                String groupID = db.writeNewGroup(groupName, listUidMember, "avtdefault.jpg", true, "", "");
                Intent intent = new Intent(AddGroupActivity.this, ChatMessageActivity.class);
                Bundle bundleSent = new Bundle();
                bundleSent.putString("idGroup", groupID);
                bundleSent.putString("nameGroup", groupName);
                bundleSent.putString("imageGroup", "avtdefault.jpg");
                String uidChat = mAuth.getCurrentUser().getUid();
                bundleSent.putString("uidChat", uidChat);
                intent.putExtras(bundleSent);
                startActivity(intent);
            }
        });
    }

    //search

    private void searchUserByName(String textSearch){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFriend.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(!mAuth.getCurrentUser().getUid().equals(user.getUid()) && user.getName().toLowerCase(Locale.ROOT).contains(textSearch)){
                        listFriend.add(new AddGroupUser(user, false));
                    }
                }
                addGroupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchFullUser(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFriend.clear();
                int i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    i++;
                    if(i == 8){
                        break;
                    }
                    User user = dataSnapshot.getValue(User.class);
                    if(!mAuth.getCurrentUser().getUid().equals(user.getUid())){
                        listFriend.add(new AddGroupUser(user, false));
                    }
                }
                addGroupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUserList(String newText) {
        if(newText.isEmpty()){
            searchFullUser();
        }
        else{
            searchUserByName(newText);
        }
    }

    //end search

    private final AddSelectedListListener addSelectedListListener = new AddSelectedListListener() {
        @Override
        public void onMemberClicked(int pos, boolean isCheck) {
            if(isCheck){
                Toast.makeText(AddGroupActivity.this, "True", Toast.LENGTH_SHORT).show();
                listFriend.get(pos).setChecked(true);
//                addGroupAdapter.notifyDataSetChanged();
                addGroupAdapter.notifyItemChanged(pos);
                listSelected.add(listFriend.get(pos));
                selectedGroupAdapter.notifyItemInserted(listSelected.size()-1);
            }
            else {
                Toast.makeText(AddGroupActivity.this, "False", Toast.LENGTH_SHORT).show();

                listFriend.get(pos).setChecked(false);
//                addGroupAdapter.notifyDataSetChanged();
                addGroupAdapter.notifyItemChanged(pos);
                int i=0;
                for(i=0;i<listSelected.size();i++){
                    if(listFriend.get(pos).getInfo().getUid()== listSelected.get(i).getInfo().getUid()){
                        break;
                    }
                }
                listSelected.remove(i);
                selectedGroupAdapter.notifyItemRemoved(i);
            }

            if(listSelected.size() !=0)
                layout_selectedMember.setVisibility(View.VISIBLE);
            else layout_selectedMember.setVisibility(View.INVISIBLE);
            textView_numSelected.setText("Selected: " + String.valueOf(listSelected.size()));
        }

    };

    private final AddSelectedListListener removeSelectedListener = new AddSelectedListListener() {
        @Override
        public void onMemberClicked(int pos, boolean isCheck) {
            for( int i = 0; i < listFriend.size(); i++) {
                if (pos< listSelected.size() && listFriend.get(i).getInfo().getUid() == listSelected.get(pos).getInfo().getUid()) {
                    listSelected.remove(pos);
                    selectedGroupAdapter.notifyItemRemoved(pos);
                    listFriend.get(i).setChecked(false);
//                    addGroupAdapter.notifyDataSetChanged();
                    addGroupAdapter.notifyItemChanged(i);
                    break;
                }
            }

            if (listSelected.size() == 0) {
                layout_selectedMember.setVisibility(View.INVISIBLE);
            }
            textView_numSelected.setText("Selected: " + String.valueOf(listSelected.size()));
        }
    };

    private void readListUser(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFriend.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    listFriend.add(new AddGroupUser(user ,false));
                }

                addGroupAdapter = new AddGroupAdapter(AddGroupActivity.this, listFriend, addSelectedListListener);
                recyclerView_listFriend.setAdapter(addGroupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
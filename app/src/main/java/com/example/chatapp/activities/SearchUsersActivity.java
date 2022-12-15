package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.interfaces.RecyclerViewInterface;
import com.example.chatapp.adapters.SearchUserAdapter;
import com.example.chatapp.db.DbReference;
import com.example.chatapp.models.Group;
import com.example.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SearchUsersActivity extends AppCompatActivity {
    private EditText etSearchUser;
    private RecyclerView rcvSearchUser;
    private ImageView btnBack;
    private ArrayList<User> listUser;
    private SearchUserAdapter searchUserAdapter;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        getSupportActionBar().hide();

        etSearchUser = (EditText) findViewById(R.id.etSearchUser);
        etSearchUser.requestFocus();

        rcvSearchUser = (RecyclerView) findViewById(R.id.rcvSearchUser);
        btnBack = (ImageView) findViewById(R.id.btn_close);
        listUser = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        searchFullUser();
        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty()){
                    searchFullUser();
                }
                else{
                    searchUserByName(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchUserAdapter = new SearchUserAdapter(listUser, SearchUsersActivity.this, recyclerViewInterface);
        rcvSearchUser.setLayoutManager(new LinearLayoutManager(SearchUsersActivity.this, LinearLayoutManager.VERTICAL, false));
        rcvSearchUser.setAdapter(searchUserAdapter);

        //
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchUsersActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private final RecyclerViewInterface recyclerViewInterface = new RecyclerViewInterface() {
        @Override
        public void onItemClick(int position) {
            User user = listUser.get(position);
            ArrayList<String> listUidMember = new ArrayList<>();
            listUidMember.add(mAuth.getCurrentUser().getUid());
            listUidMember.add(user.getUid());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean check = false;
                    String idGroup = "";
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Group group = dataSnapshot.getValue(Group.class);
                        if(group.getListUidMember().equals(listUidMember)) {
                            check = true;
                            idGroup = group.getGid();
                            break;
                        }
                    }
                    Intent intent = new Intent(SearchUsersActivity.this, ChatMessageActivity.class);
                    Bundle bundleSent = new Bundle();
                    if(check){
                        bundleSent.putString("idGroup", idGroup);
                        bundleSent.putString("nameGroup", user.getName());
                        bundleSent.putString("imageGroup", user.getImage());
                        bundleSent.putString("uidChat", user.getUid());
                        intent.putExtras(bundleSent);
                        startActivity(intent);
                    }else{
                        String gid = DbReference.writeNewGroup(user.getName() ,listUidMember, user.getImage(), false, "", "");
                        bundleSent.putString("idGroup", gid);
                        bundleSent.putString("nameGroup", user.getName());
                        bundleSent.putString("imageGroup", user.getImage());
                        bundleSent.putString("uidChat", user.getUid());
                        intent.putExtras(bundleSent);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    };

    private void searchUserByName(String textSearch){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                int i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    i++;
                    if(i == 10){
                        break;
                    }
                    User user = dataSnapshot.getValue(User.class);
                    if(!mAuth.getCurrentUser().getUid().equals(user.getUid()) && user.getName().toLowerCase(Locale.ROOT).contains(textSearch)){
                        listUser.add(user);
                    }
                }
                searchUserAdapter.notifyDataSetChanged();
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
                listUser.clear();
                int i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    i++;
                    if(i == 10){
                        break;
                    }
                    //
                    User user = dataSnapshot.getValue(User.class);
                    if(!mAuth.getCurrentUser().getUid().equals(user.getUid())){
                        listUser.add(user);
                    }
                }
                searchUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
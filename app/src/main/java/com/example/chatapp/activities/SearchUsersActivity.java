package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
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

public class SearchUsersActivity extends Activity {
    private EditText etSearchUser;
    private RecyclerView rcvSearchUser;
    private ArrayList<User> listUser;
    private SearchUserAdapter searchUserAdapter;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        etSearchUser = (EditText) findViewById(R.id.etSearchUser);

        rcvSearchUser = (RecyclerView) findViewById(R.id.rcvSearchUser);
        listUser = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
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

        searchUserAdapter = new SearchUserAdapter(listUser, SearchUsersActivity.this, recyclerViewInterface);
        rcvSearchUser.setLayoutManager(new LinearLayoutManager(SearchUsersActivity.this, LinearLayoutManager.VERTICAL, false));
        rcvSearchUser.setAdapter(searchUserAdapter);
    }

    private final RecyclerViewInterface recyclerViewInterface = new RecyclerViewInterface() {
        @Override
        public void onItemClick(int position) {
            Toast.makeText(SearchUsersActivity.this, "Show", Toast.LENGTH_SHORT).show();
            User user = listUser.get(position);
            ArrayList<String> listUidMember = new ArrayList<>();
            listUidMember.add(mAuth.getCurrentUser().getUid());
            listUidMember.add(user.getUid());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Group group = dataSnapshot.getValue(Group.class);
                        if(group.getListUidMember().equals(listUidMember)) {
                            return;
                        }
                        else{
                            DbReference.writeNewGroup(user.getName() ,listUidMember, user.getImage(), false, "Khum co");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    };

    private ArrayList<String> readListUserGroups(){
        databaseReference = FirebaseDatabase.getInstance().getReference("UserGroups").child(mAuth.getCurrentUser().getUid()).child("listGid");
        ArrayList<String> listGroup = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String idGroup = dataSnapshot.getValue(String.class);
                    listGroup.add(idGroup);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return listGroup;
    }
}
package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.models.User;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        etSearchUser = (EditText) findViewById(R.id.etSearchUser);
        etSearchUser.requestFocus();

        rcvSearchUser = (RecyclerView) findViewById(R.id.rcvSearchUser);
        listUser = new ArrayList<>();
//        listUser.add(new User("Gy0Q3TqGRSTKWJmlvlfAbhiiVWx1", "Tường", "1", true));
//        listUser.add(new User("Gy0Q3TqGRSTKWJmlvlfAbhiiVWx1", "Trường", "1", true));

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    listUser.add(user);
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
            Toast.makeText(SearchUsersActivity.this, "show", Toast.LENGTH_SHORT).show();
        }
    };
}
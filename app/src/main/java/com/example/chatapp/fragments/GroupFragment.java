package com.example.chatapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapters.GroupsAdapter;
import com.example.chatapp.R;
import com.example.chatapp.interfaces.RecyclerViewInterface;
import com.example.chatapp.activities.AddGroupActivity;
import com.example.chatapp.activities.ChatMessageActivity;
import com.example.chatapp.adapters.ChatUsersAdapter;
import com.example.chatapp.db.DbReference;
import com.example.chatapp.models.AddGroupUser;
import com.example.chatapp.models.Group;
import com.example.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;

public class GroupFragment extends Fragment {
    private LinearLayout llGroups;
    private RecyclerView recyclerView_GroupList;
    private ArrayList<Group> groupsData = new ArrayList<>();
    private GroupsAdapter groupsAdapter;
    private ChatUsersAdapter chatUsersAdapter;
    private ImageView imageView_btnGroup;
    private SearchView searchView_Search;
    //
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llGroups = (LinearLayout) inflater.inflate(R.layout.fragment_group, container, false);
        imageView_btnGroup= llGroups.findViewById(R.id.imageView_btnGroup);
        searchView_Search = llGroups.findViewById(R.id.searchView_Search);

        //database
        mDatabase = DbReference.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        FirebaseDatabase.getInstance().getReference("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Group group = dataSnapshot.getValue(Group.class);
//                    Toast.makeText(getActivity(), user.getUid(), Toast.LENGTH_SHORT).show();

                    if(group.getListUidMember().size() > 2  && group.getListUidMember().toString().contains(user.getUid()) ){
                        groupsData.add(group);
                    }
                }

                groupsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Get groups failed!", Toast.LENGTH_SHORT).show();
            }
        });

        //search group
        searchView_Search.clearFocus();
        searchView_Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()) {
                    searchGroupByName(newText);
                } else {
                    searchAllGroupOfUser();
                }
                return false;
            }
        });


//        recyclerView_GroupList = findViewById(R.id.recyclerView_GroupList);
        chatUsersAdapter = new ChatUsersAdapter(groupsData , getContext(), recyclerViewInterface);
        recyclerView_GroupList = llGroups.findViewById(R.id.recyclerView_GroupList);
        recyclerView_GroupList.setHasFixedSize(true);
        recyclerView_GroupList.setLayoutManager(new GridLayoutManager(getContext(),1));
        groupsAdapter = new GroupsAdapter(getContext(), groupsData, recyclerViewInterface);
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

    private void searchAllGroupOfUser(){
        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsData.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Group group = dataSnapshot.getValue(Group.class);

                    if(group.getListUidMember().size() > 2  && group.getListUidMember().toString().contains(mAuth.getCurrentUser().getUid()) ){
                        groupsData.add(group);
                    }
                }
                groupsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGroupByName(String textSearch){
        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupsData.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Group group = dataSnapshot.getValue(Group.class);

                    if(group.getName().toLowerCase(Locale.ROOT).contains(textSearch) && group.getListUidMember().size() > 2  && group.getListUidMember().toString().contains(mAuth.getCurrentUser().getUid()) ){
                        groupsData.add(group);
                    }
                }
                groupsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private final RecyclerViewInterface recyclerViewInterface = new RecyclerViewInterface() {
        @Override
        public void onItemClick(int position) {
            Toast.makeText(getContext(), "ItemClick", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), ChatMessageActivity.class);
            Bundle bundleSent = new Bundle();
            bundleSent.putString("idGroup", groupsData.get(position).getGid());
            bundleSent.putString("nameGroup", groupsData.get(position).getName());
            bundleSent.putString("imageGroup", groupsData.get(position).getImageId());
            String uidChat = mAuth.getCurrentUser().getUid().equals(groupsData.get(position).getListUidMember().get(0))
                    ? groupsData.get(position).getListUidMember().get(1) : groupsData.get(position).getListUidMember().get(0);
            bundleSent.putString("uidChat", uidChat);
            intent.putExtras(bundleSent);
            startActivity(intent);
        }
    };
}
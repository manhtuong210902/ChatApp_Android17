package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatapp.db.DbReference;
import com.example.chatapp.models.Group;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {
    private LinearLayout llGroups;
    private RecyclerView recyclerView_GroupList;
    private ArrayList<Group> groupsData = new ArrayList<>();
    private GroupsAdapter groupsAdapter;
    private ChatUsersAdapter chatUsersAdapter;
    private ImageView imageView_btnGroup;
    //
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llGroups = (LinearLayout) inflater.inflate(R.layout.fragment_group, container, false);
        imageView_btnGroup= llGroups.findViewById(R.id.imageView_btnGroup);

        //database
        mDatabase = DbReference.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        FirebaseDatabase.getInstance().getReference("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Group group = dataSnapshot.getValue(Group.class);
                    Toast.makeText(getActivity(), user.getUid(), Toast.LENGTH_SHORT).show();

                    if(group.getListUidMember().size()>=2  && group.getListUidMember().toString().contains(user.getUid()) ){
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
//        recyclerView_GroupList = findViewById(R.id.recyclerView_GroupList);
        chatUsersAdapter = new ChatUsersAdapter(groupsData , getContext(), recyclerViewInterface);
        recyclerView_GroupList = llGroups.findViewById(R.id.recyclerView_GroupList);
        recyclerView_GroupList.setHasFixedSize(true);
        recyclerView_GroupList.setLayoutManager(new GridLayoutManager(getContext(),1));
        groupsAdapter = new GroupsAdapter(getContext(), groupsData);
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

    private final RecyclerViewInterface recyclerViewInterface = new RecyclerViewInterface() {
        @Override
        public void onItemClick(int position) {
            Toast.makeText(getContext(), "ItemClick", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), ChatMessageActivity.class);
            Bundle bundleSent = new Bundle();
            bundleSent.putString("idGroup", groupsData.get(position).getGid());
//           bundleSent.putString("username", listChatUser.get(position).getName());
//           bundleSent.putString("avatar", listChatUser.get(position).getImageId());
            intent.putExtras(bundleSent);
            startActivity(intent);
        }
    };
}
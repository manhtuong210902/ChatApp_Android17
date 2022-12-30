package com.example.chatapp.fragments;

import static com.example.chatapp.db.DbReference.writeNewGroup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.activities.ChatMessageActivity;
import com.example.chatapp.activities.MainActivity;
import com.example.chatapp.adapters.ChatUsersAdapter;
import com.example.chatapp.adapters.OnlineUsersAdapter;
import com.example.chatapp.R;
import com.example.chatapp.db.FCMSend;
import com.example.chatapp.interfaces.RecyclerViewInterface;
import com.example.chatapp.activities.SearchUsersActivity;
import com.example.chatapp.db.DbReference;
import com.example.chatapp.models.Group;
import com.example.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ChatHomeFragment extends Fragment {
    private LinearLayout llHomeChats, searchBtn;
    private RecyclerView recyclerViewOnlineUser;
    private RecyclerView recyclerViewChatUser;
    private ArrayList<Group> listChatUser;
    private ArrayList<User> listUser;
    private String userAnother;
    private OnlineUsersAdapter onlineUsersAdapter;
    private ChatUsersAdapter chatUsersAdapter;

    //database
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private Uri imageUri;

    //test
    private TextView tvChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //database
        mDatabase = DbReference.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        llHomeChats = (LinearLayout) inflater.inflate(R.layout.fragment_home_chat, container, false);
        recyclerViewOnlineUser = (RecyclerView) llHomeChats.findViewById(R.id.c_rcvOnlineUser);
        recyclerViewChatUser = (RecyclerView) llHomeChats.findViewById(R.id.c_rcvChatUser);
        searchBtn = (LinearLayout) llHomeChats.findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchUsersActivity.class);
                startActivity(intent);
            }
        });

//        ShareReferen
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean showNotification = sharedPreferences.getBoolean("notification", true);
        boolean status = sharedPreferences.getBoolean("status", true);

        if(status) {
            DbReference.writeIsOnlineUserAndGroup(mAuth.getCurrentUser().getUid(), true);
        } else {
            DbReference.writeIsOnlineUserAndGroup(mAuth.getCurrentUser().getUid(), false);
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        if(showNotification) {
                            FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("did").setValue(token);
                        }
                        // Log and toast
                        Log.i("TokenDevice", token);
//                        Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
                    }
                });

        //render list user
        listChatUser = new ArrayList<>();

        onlineUsersAdapter = new OnlineUsersAdapter(listChatUser, getContext());
        recyclerViewOnlineUser.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        chatUsersAdapter = new ChatUsersAdapter(listChatUser, getContext(), recyclerViewInterface);
        recyclerViewChatUser.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


        FirebaseDatabase.getInstance().getReference("Groups")
                .orderByChild("lastTime")
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChatUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);

                    //get uid of user other than the current user
                    if(group.getListUidMember().contains(mAuth.getCurrentUser().getUid()) && !group.getLastMessage().isEmpty()) {
                        if(group.getListUidMember().size() == 2){
                            if(group.getListUidMember().get(0).equals(mAuth.getCurrentUser().getUid())) {
                                userAnother = group.getListUidMember().get(1);
                            } else {
                                userAnother = group.getListUidMember().get(0);
                            }

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userAnother)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user = snapshot.getValue(User.class);
                                            Log.i("user ", "a" + user.getIsOnline());
                                            group.setName(user.getName());
                                            group.setImageId(user.getImage());
                                            group.setOnline(user.getIsOnline());

                                            chatUsersAdapter.notifyDataSetChanged();
                                            onlineUsersAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        listChatUser.add(0, group);
                    }

                }

                recyclerViewOnlineUser.setAdapter(onlineUsersAdapter);
                recyclerViewChatUser.setAdapter(chatUsersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Get groups failed!", Toast.LENGTH_SHORT).show();
            }
        });

        return llHomeChats;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        listChatUser.clear();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        listChatUser.clear();
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && data != null) {
            imageUri = data.getData();

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            uploadImageToFirebase(fileInBytes);
        }
    }

    private String uploadImageToFirebase(byte[] fileInBytes) {
        final String imageId = UUID.randomUUID().toString() + ".jpg";
        StorageReference imgRef = mStorage.child("images/" + imageId);
        UploadTask uploadTask = imgRef.putBytes(fileInBytes);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Upload image failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Upload image successes!", Toast.LENGTH_SHORT).show();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DbReference.writeImageUser(uid, imageId);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("TAG", "Upload is " + progress + "% done");
            }
        });
        return imageId;
    }


    private final RecyclerViewInterface recyclerViewInterface = new RecyclerViewInterface() {
        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(getContext(), ChatMessageActivity.class);
            Bundle bundleSent = new Bundle();
            bundleSent.putString("idGroup", listChatUser.get(position).getGid());
            bundleSent.putString("nameGroup", listChatUser.get(position).getName());
            bundleSent.putString("imageGroup", listChatUser.get(position).getImageId());
            String uidChat = mAuth.getCurrentUser().getUid().equals(listChatUser.get(position).getListUidMember().get(0))
                    ? listChatUser.get(position).getListUidMember().get(1) : listChatUser.get(position).getListUidMember().get(0);
            bundleSent.putString("uidChat", uidChat);
            intent.putExtras(bundleSent);
            startActivity(intent);
        }
    };

    //khó quá
    private void getGroup(String id){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
//                get uid of user other than the current user
                if (group.getListUidMember().size() == 2 && !group.getLastMessage().isEmpty()) {
                    if (group.getListUidMember().get(0).equals(mAuth.getCurrentUser().getUid())) {
                        userAnother = group.getListUidMember().get(1);
                    } else {
                        userAnother = group.getListUidMember().get(0);
                    }

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(userAnother)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    group.setName(user.getName());
                                    group.setImageId(user.getImage());
                                    group.setOnline(user.getIsOnline());
                                    chatUsersAdapter.notifyDataSetChanged();
                                    onlineUsersAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    listChatUser.add(group);
                }
                else{
                    listChatUser.add(group);
                    chatUsersAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

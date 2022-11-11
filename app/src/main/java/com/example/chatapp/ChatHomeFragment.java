package com.example.chatapp;

import static com.example.chatapp.db.DbReference.writeNewGroup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.db.DbReference;
import com.example.chatapp.models.Group;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ChatHomeFragment extends Fragment {
    private LinearLayout llHomeChats;
    private RecyclerView recyclerViewOnlineUser;
    private RecyclerView recyclerViewChatUser;
    private ArrayList<Group> listChatUser;
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

        listChatUser = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Groups").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    listChatUser.add(group);
                }
                chatUsersAdapter.notifyDataSetChanged();
                onlineUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Get groups failed!", Toast.LENGTH_SHORT).show();
            }
        });


        onlineUsersAdapter = new OnlineUsersAdapter(listChatUser, getContext());
        recyclerViewOnlineUser.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewOnlineUser.setAdapter(onlineUsersAdapter);

        chatUsersAdapter = new ChatUsersAdapter(listChatUser, getContext());
        recyclerViewChatUser.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewChatUser.setAdapter(chatUsersAdapter);

        tvChats = (TextView) llHomeChats.findViewById(R.id.c_tvChats);
        tvChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                Toast.makeText(getActivity(), "clicked!", Toast.LENGTH_SHORT).show();
                startActivityForResult(galleryIntent, 200);
            }
        });

        return llHomeChats;
    }

    @Override
    public void onStart() {
        super.onStart();
//        mAuth.signInWithEmailAndPassword("test2@gmail.com", "123456");
//        ArrayList<String> listUidMember = new ArrayList<>();
//        listUidMember.add(mAuth.getCurrentUser().getUid());
//        listUidMember.add("ddVfY1n6kEN2UK0sdX8wBKFn0Mg1");
//        writeGroupTEST("TRUONGG", listUidMember, "f2.jpeg", true, "Khum co");

    }

    private void loginAndWriteUserTEST(String email, String password, String name, String image, boolean isOnline) {
        mAuth.signInWithEmailAndPassword(email, password);
        String uid = mAuth.getCurrentUser().getUid();
        Toast.makeText(getActivity(), uid, Toast.LENGTH_LONG).show();
        if(image == null || image == "") {
            image = "avtdefault.jpg";
        }
        DbReference.writeNewUser(uid, name, image, isOnline);
        mAuth.signOut();
    }

    private void writeGroupTEST(String name, ArrayList<String> listUidMember, String imageId, boolean isOnline, String lastMessage) {
        writeNewGroup(name, listUidMember, imageId, isOnline, lastMessage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200 && data != null ) {
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
        final String imageId = UUID.randomUUID().toString()+".jpg";
        StorageReference imgRef = mStorage.child("images/"+imageId);
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

}

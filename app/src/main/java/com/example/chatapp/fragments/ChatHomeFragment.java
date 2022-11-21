package com.example.chatapp.fragments;

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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.activities.ChatMessageActivity;
import com.example.chatapp.adapters.ChatUsersAdapter;
import com.example.chatapp.adapters.OnlineUsersAdapter;
import com.example.chatapp.R;
import com.example.chatapp.interfaces.RecyclerViewInterface;
import com.example.chatapp.activities.SearchUsersActivity;
import com.example.chatapp.db.DbReference;
import com.example.chatapp.models.Group;
import com.example.chatapp.models.User;
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
    private SearchView svSearchUser;
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
        svSearchUser = (SearchView) llHomeChats.findViewById(R.id.c_svSearch);
        svSearchUser.setFocusable(true);

        svSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchUsersActivity.class);
                startActivity(intent);
            }
        });

        listChatUser = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Groups").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChatUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);

                    //get uid of user other than the current user
                    if(group.getListUidMember().contains(mAuth.getCurrentUser().getUid())) {
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
                                            Log.i("user ", user.getName());
                                            Log.i("user ", user.getImage());
                                            group.setName(user.getName());
                                            group.setImageId(user.getImage());

                                            chatUsersAdapter.notifyDataSetChanged();
                                            onlineUsersAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        listChatUser.add(group);
                    }

                }

                chatUsersAdapter.notifyDataSetChanged();
                onlineUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Get groups failed!", Toast.LENGTH_SHORT).show();
            }
        });


        //change name & img ^^!


        onlineUsersAdapter = new OnlineUsersAdapter(listChatUser, getContext());
        recyclerViewOnlineUser.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewOnlineUser.setAdapter(onlineUsersAdapter);


        chatUsersAdapter = new ChatUsersAdapter(listChatUser, getContext(), recyclerViewInterface);
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

    private void getListUser(ArrayList<Group> listChatUser1) {
        listUser = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for(int i=0; i<listChatUser.size(); i++) {
                        if(listChatUser.get(i).getListUidMember().contains(user.getUid()) && !user.getUid().equals(mAuth.getCurrentUser().getUid())) {
                            listUser.add(user);
                        }
                    }
                }
                chatUsersAdapter.notifyDataSetChanged();
                onlineUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Log.i("size list user", Integer.toString(listUser.size()));
//        for(int i=0; i<listChatUser.size(); i++) {
//            for(int j=0; j<listUser.size(); j++) {
//                if(listChatUser.get(i).getListUidMember().contains(listUser.get(j).getUid())) {
//                    listChatUser.get(i).setName(listUser.get(j).getName());
//                    listChatUser.get(i).setImageId(listUser.get(j).getImage());
//                }
//            }
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
            //TEST HERE :D
        // please call one times and comment function below :3
//         TEST();
//        ArrayList<String> listGid = new ArrayList<>();
//        listGid.add("newGid");
//        DbReference.updateListGroupForUserGroups(mAuth.getCurrentUser().getUid(), listGid);
    }

    private void TEST() {
        ArrayList<String> listUidMember2 = new ArrayList<>();
        listUidMember2.add(mAuth.getCurrentUser().getUid());
        listUidMember2.add("Gy0Q3TqGRSTKWJmlvlfAbhiiVWx1");
        String gid2 = DbReference.writeNewGroup("Nguyễn Mạnh Tường(Gr)", listUidMember2, "avtdefault.jpg", true, "Khum co1");

        ArrayList<String> listUidMember3 = new ArrayList<>();
        listUidMember3.add(mAuth.getCurrentUser().getUid());
        listUidMember3.add("tSeDuvDRozXGtXgYsjEE7tIoIG13");
        String gid3 =DbReference.writeNewGroup("Nguyễn Thanh Tùng(Gr)", listUidMember3, "avtdefault.jpg", true, "Khum co2");

        ArrayList<String> listUidMember4 = new ArrayList<>();
        listUidMember4.add(mAuth.getCurrentUser().getUid());
        listUidMember4.add("pGBznA03v1Z86ebrqCOvemO4rCN2");
        String gid4 =DbReference.writeNewGroup("Nguyễn Lam Trường(Gr)", listUidMember4, "avtdefault.jpg", true, "Khum co3");

        ArrayList<String> listUidMember5 = new ArrayList<>();
        listUidMember5.add(mAuth.getCurrentUser().getUid());
        listUidMember5.add("32D6AxicV8NRsq0z9cKQ9hWNZpv2");
        String gid5 = DbReference.writeNewGroup("Nguyễn Anh(Gr)", listUidMember5, "avtdefault.jpg", true, "Khum co4");

        String uid = mAuth.getCurrentUser().getUid();
        ArrayList<String> listGid = new ArrayList<>();
        listGid.add(gid2);
        listGid.add(gid3);
        listGid.add(gid4);
        listGid.add(gid5);
        DbReference.writeNewUserGroups(uid, listGid);
    }

    private void loginAndWriteUserTEST(String email, String password, String name, String image, boolean isOnline) {
        mAuth.signInWithEmailAndPassword(email, password);
        String uid = mAuth.getCurrentUser().getUid();
        Toast.makeText(getActivity(), uid, Toast.LENGTH_LONG).show();
        if (image == null || image == "") {
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
            Toast.makeText(getContext(), "ItemClick", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), ChatMessageActivity.class);
            Bundle bundleSent = new Bundle();
            bundleSent.putString("idGroup", listChatUser.get(position).getGid());
            bundleSent.putString("nameGroup", listChatUser.get(position).getName());
            bundleSent.putString("imageGroup", listChatUser.get(position).getImageId());
            intent.putExtras(bundleSent);
            startActivity(intent);
        }
    };
}

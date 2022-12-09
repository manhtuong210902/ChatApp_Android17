package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.adapters.SearchUserAdapter;
import com.example.chatapp.fragments.ChatHomeFragment;
import com.example.chatapp.interfaces.RecyclerViewInterface;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUserActivity extends Activity {
    ImageView btn_close;
    CircleImageView circleImageView_Avatar, civOnlineCircle;
    TextView textview_Name, textview_active, textview_UserName, textview_EmailUser;
    RecyclerView recyclerView_GroupMember;
    LinearLayout llUserInfo, llGroupMember;
    Button btn_leaveGroup;
    private FirebaseAuth mAuth;
    private ArrayList<User> listMember;
    private SearchUserAdapter listMemberAdapter;
    private  String imageGroup;
    private String idGroup;
    AlertDialog.Builder dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        mAuth = FirebaseAuth.getInstance();
        textview_UserName = findViewById(R.id.textview_UserName);
        textview_active = findViewById(R.id.textview_active);
        textview_EmailUser = findViewById(R.id.textview_EmailUser);
        llUserInfo = findViewById(R.id.llUserInfo);
        llGroupMember = findViewById(R.id.llGroupMember);
        civOnlineCircle = findViewById(R.id.civOnlineCircle);
        btn_leaveGroup = findViewById(R.id.btn_leaveGroup);

        recyclerView_GroupMember = findViewById(R.id.recyclerView_GroupMember);
        listMember = new ArrayList<>();
        listMemberAdapter = new SearchUserAdapter(listMember, ProfileUserActivity.this, recyclerViewInterface);
        recyclerView_GroupMember.setLayoutManager(new LinearLayoutManager(ProfileUserActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView_GroupMember.setAdapter(listMemberAdapter);
        //get info
        Bundle bundleRev = getIntent().getExtras();
        idGroup = bundleRev.getString("idGroup");
        String nameGroup = bundleRev.getString("nameGroup");
        imageGroup = bundleRev.getString("imageGroup");

        //get user of group
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Groups").child(idGroup);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> listUidMember = new ArrayList<>();
                Group A = snapshot.getValue(Group.class);
                listUidMember = A.getListUidMember();
                if(listUidMember.size() == 2){
                    llUserInfo.setVisibility(View.VISIBLE);
                    llGroupMember.setVisibility(View.INVISIBLE);
                    String idUser = listUidMember.get(0) != mAuth.getCurrentUser().getUid() ? listUidMember.get(1) : listUidMember.get(0);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef1 = database.getReference("Users").child(idUser);
                    myRef1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            textview_UserName.setText(user.getName());
                            textview_EmailUser.setText(user.getEmail());
                            if(!user.isOnline()) {
                                civOnlineCircle.setBorderColor(ProfileUserActivity.this.getResources().getColor(R.color.yellow_circle));
                                textview_active.setText("Offline");
                            } else {
                                civOnlineCircle.setBorderColor(ProfileUserActivity.this.getResources().getColor(R.color.green_circle));
                                textview_active.setText("Active");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                else {
                    llUserInfo.setVisibility(View.INVISIBLE);
                    llGroupMember.setVisibility(View.VISIBLE);
                    if(!A.isOnline()) {
                        civOnlineCircle.setBorderColor(ProfileUserActivity.this.getResources().getColor(R.color.yellow_circle));
                        textview_active.setText("Offline");
                    } else {
                        civOnlineCircle.setBorderColor(ProfileUserActivity.this.getResources().getColor(R.color.green_circle));
                        textview_active.setText("Active");
                    }
                    for(int i=0;i< listUidMember.size();i++){
                        String idUser = listUidMember.get(i) ;
                        if(idUser == mAuth.getCurrentUser().getUid())
                            continue;
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef2 = database.getReference("Users").child(idUser);
                        myRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                listMember.add(user);
                                listMemberAdapter.notifyItemInserted(listMember.size()-1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }

                    listMemberAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //avatar
        circleImageView_Avatar = findViewById(R.id.circleImageView_Avatar);
        FirebaseStorage.getInstance().getReference().child("images/"+imageGroup)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(circleImageView_Avatar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {}
                });
        //Name
        textview_Name = findViewById(R.id.textview_Name);
        textview_Name.setText(nameGroup);

        //back button
        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //left group btn
        dialog = new AlertDialog.Builder(ProfileUserActivity.this );
        btn_leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setTitle("Leave group")
                    .setMessage("Are you sure want to leave the group?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(ProfileUserActivity.this, "Yes", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Groups").child(idGroup);
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Group group = snapshot.getValue(Group.class);
                                    ArrayList<String> listUser = group.getListUidMember();
                                    listUser.remove(mAuth.getCurrentUser().getUid());
                                    Map<String, Object> userValues = group.toMap();
                                    Map<String, Object> userUpdates = new HashMap<>();
                                    userUpdates.put("/Groups/" + idGroup, userValues);
                                    FirebaseDatabase.getInstance().getReference().updateChildren(userUpdates);
                                    Intent intent = new Intent(ProfileUserActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(ProfileUserActivity.this, "No", Toast.LENGTH_SHORT).show();
                        }
                    });
                AlertDialog alert = dialog.create();
                alert.setTitle("Leave group");
                alert.show();
            }
        });
    }

    private final RecyclerViewInterface recyclerViewInterface = new RecyclerViewInterface() {
        @Override
        public void onItemClick(int position) {
            if(mAuth.getCurrentUser().getUid() != listMember.get(position).getUid())
            {
                llUserInfo.setVisibility(View.VISIBLE);
                llGroupMember.setVisibility(View.INVISIBLE);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef1 = database.getReference("Users").child(listMember.get(position).getUid());
                myRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        textview_UserName.setText(user.getName());
                        textview_EmailUser.setText(user.getEmail());
                        textview_Name.setText(user.getName());
                        FirebaseStorage.getInstance().getReference().child("images/"+user.getImage())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(circleImageView_Avatar);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {}
                            });
                        if(!user.isOnline()) {
                            civOnlineCircle.setBorderColor(ProfileUserActivity.this.getResources().getColor(R.color.yellow_circle));
                            textview_active.setText("Offline");
                        } else {
                            civOnlineCircle.setBorderColor(ProfileUserActivity.this.getResources().getColor(R.color.green_circle));
                            textview_active.setText("Active");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        }
    };
}
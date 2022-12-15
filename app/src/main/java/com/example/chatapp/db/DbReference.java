package com.example.chatapp.db;


import androidx.annotation.NonNull;

import com.example.chatapp.models.Group;
import com.example.chatapp.models.Message;
import com.example.chatapp.models.User;
import com.example.chatapp.models.UserGroups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DbReference {
    static private DatabaseReference mDatabase;

    static public DatabaseReference getInstance() {
        if(mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            return mDatabase;
        }
        return mDatabase;
    }

    static public void writeNewUser(String uid, String email, String name, String image, boolean isOnline, String did) {
        mDatabase = getInstance();
        User user = new User(uid,email, name, image, isOnline, did);

        Map<String, Object> userValues = user.toMap();

        Map<String, Object> userUpdates = new HashMap<>();

        userUpdates.put("/Users/" + uid, userValues);

        mDatabase.updateChildren(userUpdates);

    }

    static public void writeImageUser(String uid, String imageId) {
        mDatabase.child("Users").child(uid).child("image").setValue(imageId);
    }

    static public void writeIsOnlineUserAndGroup(String uid, boolean isOnline) {
        mDatabase.child("Users").child(uid).child("isOnline").setValue(isOnline);

        FirebaseDatabase.getInstance().getReference("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    //get uid of user other than the current user

                    if(group.getListUidMember().contains(uid)) {
                        mDatabase.child("Groups").child(group.getGid()).child("isOnline").setValue(isOnline);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    static public String writeNewGroup(String name, ArrayList<String> listUidMember, String imageId, boolean isOnline, String lastMessage, String lastTime) {
        String gid = mDatabase.child("Groups").push().getKey(); //groupId

        Group group = new Group(gid, name, listUidMember, imageId, isOnline, lastMessage, lastTime);

        Map<String, Object> groupValues = group.toMap();

        Map<String, Object> groupUpdates = new HashMap<>();

        groupUpdates.put("/Groups/" + gid, groupValues);

        mDatabase.updateChildren(groupUpdates);
        return gid;
    }

    //a user have many groups.
    static public void writeNewUserGroups(String uid, ArrayList<String> listGid) {
        UserGroups group = new UserGroups(uid, listGid);

        Map<String, Object> userGroupsValues = group.toMap();

        Map<String, Object> userGroupsUpdates = new HashMap<>();

        userGroupsUpdates.put("/UserGroups/" + uid, userGroupsValues);

        mDatabase.updateChildren(userGroupsUpdates);

    }

    static public void updateListGroupForUserGroups(String uid, ArrayList<String> listGid) {
        FirebaseDatabase.getInstance().getReference("UserGroups").child(uid).child("listGid").setValue(listGid);
    }


    static public void writeNewMessage(String uid, String gid, ArrayList<String> listMemberSeen, boolean isImage, String imageId) {
        String mid = mDatabase.child("Messages").push().getKey(); //messageId

        Message message = new Message(mid, uid, gid, listMemberSeen, isImage, imageId);

        Map<String, Object> messageValues = message.toMap();

        Map<String, Object> messageUpdates = new HashMap<>();

        messageUpdates.put("/Messages/" + mid, messageValues);

        mDatabase.updateChildren(messageUpdates);

    }
}

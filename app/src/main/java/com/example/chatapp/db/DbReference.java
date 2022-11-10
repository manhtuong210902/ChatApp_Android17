package com.example.chatapp.db;

import com.example.chatapp.models.Group;
import com.example.chatapp.models.Message;
import com.example.chatapp.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    static public void writeNewUser(String uid, String name, String image, boolean isOnline) {
        User user = new User(uid, name, image, isOnline);

        Map<String, Object> userValues = user.toMap();

        Map<String, Object> userUpdates = new HashMap<>();

        userUpdates.put("/Users/" + uid, userValues);

        mDatabase.updateChildren(userUpdates);

    }

    static public void writeImageUser(String uid, String imageId) {
        mDatabase.child("Users").child(uid).child("image").setValue(imageId);
    }

    static public void writeNewGroup(String name, ArrayList<String> listUidMember, String imageId, boolean isOnline, String lastMessage) {
        String gid = mDatabase.child("Groups").push().getKey(); //groupId

        Group group = new Group(gid, name, listUidMember, imageId, isOnline, lastMessage);

        Map<String, Object> groupValues = group.toMap();

        Map<String, Object> groupUpdates = new HashMap<>();

        groupUpdates.put("/Groups/" + gid, groupValues);

        mDatabase.updateChildren(groupUpdates);

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

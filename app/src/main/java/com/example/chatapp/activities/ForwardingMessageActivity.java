package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.adapters.AddGroupAdapter;
import com.example.chatapp.adapters.SelectedGroupAdapter;
import com.example.chatapp.db.DbReference;
import com.example.chatapp.db.FCMSend;
import com.example.chatapp.interfaces.AddSelectedListListener;
import com.example.chatapp.models.AddGroupUser;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.models.Group;
import com.example.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForwardingMessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView_listFriend;
    private List<AddGroupUser> listFriend, listSelected;
    private String chatMessage,typeMessage;
    DatabaseReference databaseReference;
    private ArrayList<String> listIdGroup;
    private ArrayList<Group> listGroup;
    private String didUserChat;
    private AddGroupAdapter addGroupAdapter;
    private SelectedGroupAdapter selectedGroupAdapter;
    LinearLayout layout_selectedMember, linearLayout;
    ImageView btn_close;
    TextView textView_Title;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    ImageView imageView_btnGroup;
    SearchView searchView_SearchUser;
    private DbReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forwarding_message);
        getSupportActionBar().hide();
        listGroup=new ArrayList<>();
        listIdGroup=new ArrayList<>();
        listFriend = new ArrayList<>();
        listSelected = new ArrayList<>();
        mDatabase = DbReference.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        recyclerView_listFriend = findViewById(R.id.recyclerView_listFriendfm);
        layout_selectedMember = findViewById(R.id.layout_selectedMemberfm);
        linearLayout = findViewById(R.id.linearLayoutfm);
        textView_Title = findViewById(R.id.textView_Titlefm);
        imageView_btnGroup = findViewById(R.id.imageView_btnGroupfm);
        String currID = mAuth.getCurrentUser().getUid();

        recyclerView_listFriend.setLayoutManager(new GridLayoutManager(ForwardingMessageActivity.this, 1));
        addGroupAdapter = new AddGroupAdapter(ForwardingMessageActivity.this, listFriend, addSelectedListListener);
        recyclerView_listFriend.setAdapter(addGroupAdapter);
        searchFullUser();

        //search user
        searchView_SearchUser = findViewById(R.id.searchView_SearchUserfm);
        searchView_SearchUser.clearFocus();
        searchView_SearchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUserList(newText);
                return false;
            }
        });

        //end search user

        selectedGroupAdapter = new SelectedGroupAdapter(ForwardingMessageActivity.this, listSelected, removeSelectedListener);

        layout_selectedMember.setVisibility(View.INVISIBLE);
        btn_close = findViewById(R.id.btn_closefm);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        db = new DbReference();

        FirebaseDatabase.getInstance().getReference("Groups")
                .orderByChild("lastTime")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listGroup.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Group group = dataSnapshot.getValue(Group.class);
                            if(group.getListUidMember().contains(mAuth.getCurrentUser().getUid()) && !group.getLastMessage().isEmpty()) {
                                listGroup.add(0, group);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        imageView_btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleRev = getIntent().getExtras();
                chatMessage=bundleRev.getString("chatMessage");
                typeMessage=bundleRev.getString("typeMessage");
                Date date = new Date();
                    Timestamp timestamp = new Timestamp(date.getTime());
                    String currentTime = timestamp.toString();

                    ChatMessage chat = new ChatMessage(currentTime, chatMessage, mAuth.getCurrentUser().getUid(), typeMessage,"");
                    for (int i=0;i<listSelected.size();i++){
                        String id =listSelected.get(i).getInfo().getUid();
                        for(int j=0;j<listGroup.size();j++){
                            if(listGroup.get(j).getListUidMember().contains(id)){
                                listIdGroup.add(listGroup.get(j).getGid());
                            }
                        }
                    }
                    for(int i=0;i<listIdGroup.size();i++){
                        sendMessage(chat,listIdGroup.get(i));
                    }
                    onBackPressed();

            }
        });
    }

    //search
    private void sendMessage(ChatMessage chat, String idGroup){
        Bundle bundleRev = getIntent().getExtras();
        didUserChat = bundleRev.getString("didUserChat");
        //path: ChatMessage
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String messageId = ref.child("ChatMessage").child(idGroup).push().getKey();
        chat.setMessageId(messageId);
        Map<String, Object> messUpdates = new HashMap<>();

        Map<String, Object> messValues = chat.toMap();
        //path/ChatMessage/idGroup/messageId
        messUpdates.put("/ChatMessage/" + idGroup + "/" + messageId, messValues);
        ref.updateChildren(messUpdates);


        DatabaseReference refGroups = FirebaseDatabase.getInstance().getReference("Groups").child(idGroup);
        DatabaseReference refMessageGroup = refGroups.child("lastMessage");
        DatabaseReference refTimeGroup = refGroups.child("lastTime");
        refTimeGroup.setValue(chat.getMessageTime());
        String notification = "";

        if(chat.getTypeMessage().equals("image")){
            refMessageGroup.setValue("image");
            notification = "sent a picture";
            // :(
        }
        else if(chat.getTypeMessage().equals("file")){
            refMessageGroup.setValue("file pdf");
            notification = "sent a file";
        }
        else{
            refMessageGroup.setValue(chat.getMessage());
            notification = chat.getMessage();
            // :(
        }

        String finalNotification = notification;


        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        FCMSend.pushNotification(getApplicationContext(), didUserChat, user.getName(), finalNotification);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void searchUserByName(String textSearch){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFriend.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(!mAuth.getCurrentUser().getUid().equals(user.getUid()) && user.getName().toLowerCase(Locale.ROOT).contains(textSearch)){
                        for(int j=0;j<listGroup.size();j++) {
                            if(listGroup.get(j).getListUidMember().contains(user.getUid()))
                                listFriend.add(new AddGroupUser(user, false));
                        }
                    }
                }
                addGroupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchFullUser(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFriend.clear();
                int i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    i++;
                    if(i == 8){
                        break;
                    }
                    User user = dataSnapshot.getValue(User.class);
                    if(!mAuth.getCurrentUser().getUid().equals(user.getUid())){
                        for(int j=0;j<listGroup.size();j++) {
                            if(listGroup.get(j).getListUidMember().contains(user.getUid()) && listGroup.get(j).getListUidMember().size()==2)
                                listFriend.add(new AddGroupUser(user, false));
                        }
                    }
                }
                addGroupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUserList(String newText) {
        if(newText.isEmpty()){
            searchFullUser();
        }
        else{
            searchUserByName(newText);
        }
    }

    //end search

    private final AddSelectedListListener addSelectedListListener = new AddSelectedListListener() {
        @Override
        public void onMemberClicked(int pos, boolean isCheck) {
            if(isCheck){
                listFriend.get(pos).setChecked(true);
                addGroupAdapter.notifyItemChanged(pos);
                listSelected.add(listFriend.get(pos));
                selectedGroupAdapter.notifyItemInserted(listSelected.size()-1);
            }
            else {

                listFriend.get(pos).setChecked(false);
                addGroupAdapter.notifyItemChanged(pos);
                int i=0;
                for(i=0;i<listSelected.size();i++){
                    if(listFriend.get(pos).getInfo().getUid()== listSelected.get(i).getInfo().getUid()){
                        break;
                    }
                }
                listSelected.remove(i);
                selectedGroupAdapter.notifyItemRemoved(i);
            }

            if(listSelected.size() !=0)
                layout_selectedMember.setVisibility(View.VISIBLE);
            else layout_selectedMember.setVisibility(View.INVISIBLE);
        }

    };

    private final AddSelectedListListener removeSelectedListener = new AddSelectedListListener() {
        @Override
        public void onMemberClicked(int pos, boolean isCheck) {
            for( int i = 0; i < listFriend.size(); i++) {
                if (pos< listSelected.size() && listFriend.get(i).getInfo().getUid() == listSelected.get(pos).getInfo().getUid()) {
                    listSelected.remove(pos);
                    selectedGroupAdapter.notifyItemRemoved(pos);
                    listFriend.get(i).setChecked(false);
                    addGroupAdapter.notifyItemChanged(i);
                    break;
                }
            }

            if (listSelected.size() == 0) {
                layout_selectedMember.setVisibility(View.INVISIBLE);
            }
        }
    };


}
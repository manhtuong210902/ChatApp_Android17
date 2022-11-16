package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessageActivity extends Activity {
    private ImageView btnSend;
    private EditText etInputMessage;
    private RecyclerView rcvListChat;

    private ArrayList<ChatMessage> listChat;
    private ChatMessageAdapter adapter;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        btnSend = (ImageView) findViewById(R.id.btnSend);
        etInputMessage = (EditText) findViewById(R.id.etInputMessage);
        rcvListChat = (RecyclerView) findViewById(R.id.rcvListChat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rcvListChat.setLayoutManager(linearLayoutManager);

        Bundle bundleRev = getIntent().getExtras();
        String idGroup = bundleRev.getString("idGroup");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etInputMessage.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String currentTime = sdf.format(new Date());

                ChatMessage chat = new ChatMessage(currentTime.toString(), message, "ddVfY1n6kEN2UK0sdX8wBKFn0Mg1", true);
                sendMessage(chat , idGroup);
                etInputMessage.setText("");
            }
        });

        listChat = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(idGroup);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readMessage(idGroup);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new ChatMessageAdapter(ChatMessageActivity.this, listChat);
        rcvListChat.setAdapter(adapter);
    }

    private void sendMessage(ChatMessage chat, String idGroup){
        //path: ChatMessage
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String messageId = ref.child("ChatMessage").child(idGroup).push().getKey();

        Map<String, Object> messUpdates = new HashMap<>();

        Map<String, Object> messValues = chat.toMap();

        //path/ChatMessage/idGroup/messageId
        messUpdates.put("/ChatMessage/" + idGroup + "/" + messageId, messValues);
        ref.updateChildren(messUpdates);

        DatabaseReference refGroups = FirebaseDatabase.getInstance().getReference("Groups").child(idGroup).child("lastMessage");
        refGroups.setValue(chat.getMessage());
    }

    private void readMessage(String idGroup){
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatMessage").child(idGroup);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listChat.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatMessage chat = dataSnapshot.getValue(ChatMessage.class);
                    listChat.add(chat);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
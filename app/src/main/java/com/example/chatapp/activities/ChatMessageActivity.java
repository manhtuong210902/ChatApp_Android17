package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatMessageAdapter;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageActivity extends Activity {
    private ImageView btnSend, btnBackMain;
    private EditText etInputMessage;
    private RecyclerView rcvListChat;
    private CircleImageView civGroupImage;
    private TextView tvGroupName;

    private ArrayList<ChatMessage> listChat;
    private ChatMessageAdapter adapter;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnBackMain = (ImageView) findViewById(R.id.btnBackMain);
        etInputMessage = (EditText) findViewById(R.id.etInputMessage);
        rcvListChat = (RecyclerView) findViewById(R.id.rcvListChat);
        civGroupImage = (CircleImageView) findViewById(R.id.civGroupImage);
        tvGroupName = (TextView) findViewById(R.id.tvGroupName);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rcvListChat.setLayoutManager(linearLayoutManager);

        Bundle bundleRev = getIntent().getExtras();
        String idGroup = bundleRev.getString("idGroup");
        String nameGroup = bundleRev.getString("nameGroup");
        String imageGroup = bundleRev.getString("imageGroup");
        FirebaseStorage.getInstance().getReference().child("images/"+ imageGroup)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(civGroupImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

        tvGroupName.setText(nameGroup);

        mAuth = FirebaseAuth.getInstance();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etInputMessage.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String currentTime = sdf.format(new Date());

                ChatMessage chat = new ChatMessage(currentTime.toString(), message, mAuth.getCurrentUser().getUid(), "text");
                sendMessage(chat, idGroup);
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

        btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatMessageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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
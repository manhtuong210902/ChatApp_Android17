package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.db.DbReference;
import com.example.chatapp.db.FCMSend;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatMessageAdapter;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageActivity extends Activity {
    private ImageView btnSend, btnBackMain, btnSentImage;
    private EditText etInputMessage;
    private RecyclerView rcvListChat;
    private CircleImageView civGroupImage;
    private TextView tvGroupName;

    private ArrayList<ChatMessage> listChat;
    private ChatMessageAdapter adapter;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    private StorageTask uploadTask;
    private Uri fileUri;
    private StorageReference mStorage;
    private String idGroup;
    private String uidChat;
    private String didUserChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnBackMain = (ImageView) findViewById(R.id.btnBackMain);
        btnSentImage = (ImageView) findViewById(R.id.btnSentImage);
        etInputMessage = (EditText) findViewById(R.id.etInputMessage);
        rcvListChat = (RecyclerView) findViewById(R.id.rcvListChat);
        civGroupImage = (CircleImageView) findViewById(R.id.civGroupImage);
        tvGroupName = (TextView) findViewById(R.id.tvGroupName);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rcvListChat.setLayoutManager(linearLayoutManager);

        Bundle bundleRev = getIntent().getExtras();
        idGroup = bundleRev.getString("idGroup");
        String nameGroup = bundleRev.getString("nameGroup");
        String imageGroup = bundleRev.getString("imageGroup");
        uidChat = bundleRev.getString("uidChat");

        FirebaseDatabase.getInstance().getReference("Users").child(uidChat)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        didUserChat = user.getDid();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
        mStorage = FirebaseStorage.getInstance().getReference();

        btnSentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 200);
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200 && resultCode == RESULT_OK && data!=null){
            fileUri = data.getData();
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
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
                Toast.makeText(ChatMessageActivity.this, "Upload image failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ChatMessageActivity.this, "Upload image successes!", Toast.LENGTH_SHORT).show();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String currentTime = sdf.format(new Date());
                ChatMessage chat = new ChatMessage(currentTime.toString(),imageId, mAuth.getCurrentUser().getUid(), "image");

                sendMessage(chat, idGroup);
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
        if(chat.getTypeMessage().equals("image")){
            refGroups.setValue("image");

            // :(
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            FCMSend.pushNotification(getApplicationContext(), didUserChat, user.getName(), "Đã gửi hình ảnh");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else{
            refGroups.setValue(chat.getMessage());

            // :(
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            FCMSend.pushNotification(getApplicationContext(), didUserChat, user.getName(), chat.getMessage());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

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
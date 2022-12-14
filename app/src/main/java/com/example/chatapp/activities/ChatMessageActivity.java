package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.db.FCMSend;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatMessageAdapter;
import com.example.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

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
    private ImageView btnSend, btnBackMain, btnSentImage, btnSentEmoji, btnSentFile;
    private TextView btnDeleteMessage;
    private EditText etInputMessage;
    private RecyclerView rcvListChat;
    private CircleImageView civGroupImg;
    private TextView tvGroupName;
    private LinearLayout llChatOption;
    private  LinearLayout llSendOption;
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

    DownloadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnBackMain = (ImageView) findViewById(R.id.btnBackMain);
        btnSentImage = (ImageView) findViewById(R.id.btnSentImage);
        btnSentFile = (ImageView) findViewById(R.id.btnSentFile);
        btnSentEmoji = (ImageView) findViewById(R.id.btnSentEmoji);
        btnDeleteMessage = (TextView) findViewById(R.id.btnDeleteMessage);
        etInputMessage = (EditText) findViewById(R.id.etInputMessage);
        rcvListChat = (RecyclerView) findViewById(R.id.rcvListChat);
        civGroupImg = (CircleImageView) findViewById(R.id.civGroupImg);
        tvGroupName = (TextView) findViewById(R.id.tvGroupName);
        llChatOption = (LinearLayout) findViewById(R.id.llChatOption);
        llSendOption = (LinearLayout) findViewById(R.id.llOptionsSent);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rcvListChat.setLayoutManager(linearLayoutManager);

        Bundle bundleRev = getIntent().getExtras();
        idGroup = bundleRev.getString("idGroup");
        String nameGroup = bundleRev.getString("nameGroup");
        String imageGroup = bundleRev.getString("imageGroup");
        uidChat = bundleRev.getString("uidChat");

        //render UI cho thanh tool barr
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
                        Picasso.get().load(uri).into(civGroupImg);
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



        //handle sent image

        btnSentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 200);
            }
        });

        //handle sent file pdf
        btnSentFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 201);
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
//                contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//                fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                startActivityForResult(intent, 200);

            }
        });

        //handle sent icon
        EmojiManager.install(new GoogleEmojiProvider());
        EmojiPopup popup = EmojiPopup.Builder.fromRootView(findViewById(R.id.rlChatLayout)).build(etInputMessage);

        btnSentEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!popup.isShowing()){
                    btnSentEmoji.setImageResource(R.drawable.ic_text);
                }else{
                    btnSentEmoji.setImageResource(R.drawable.ic_emotion_happy_line);
                }
                popup.toggle();
            }
        });

        //handle sent message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                EmojiTextView emojiTextView = (EmojiTextView) LayoutInflater
//                        .from(view.getContext())
//                        .inflate(R.layout.emoji_text_view, linerLayout, false);
                String message = etInputMessage.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String currentTime = sdf.format(new Date());

                ChatMessage chat = new ChatMessage(currentTime.toString(), message, mAuth.getCurrentUser().getUid(), "text","");
                sendMessage(chat, idGroup);
                etInputMessage.setText("");
            }
        });

        listChat = new ArrayList<>();

        //read message to db
        readMessage(idGroup);

        //handle when click back
        btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatMessageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        civGroupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatMessageActivity.this, ProfileUserActivity.class);
                Bundle bundleSent = new Bundle();
                bundleSent.putString("idGroup", idGroup);
                bundleSent.putString("nameGroup", nameGroup);
                bundleSent.putString("imageGroup", imageGroup);
                intent.putExtras(bundleSent);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            if(resultCode == RESULT_OK && data != null){
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
        else if(requestCode == 201){
            if(resultCode == RESULT_OK && data != null){
                fileUri = data.getData();
                uploadFilePDF(fileUri);
            }
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
                ChatMessage chat = new ChatMessage(currentTime.toString(),imageId, mAuth.getCurrentUser().getUid(), "image","");

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

    private void uploadFilePDF(Uri fileURI){
        final String filePath = UUID.randomUUID().toString() + "|" + getFileName(fileURI);
        StorageReference fileRef = mStorage.child("files/" + filePath);
        fileRef.putFile(fileURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String currentTime = sdf.format(new Date());
                    ChatMessage chat = new ChatMessage(currentTime.toString(), filePath, mAuth.getCurrentUser().getUid(), "file","");
                    sendMessage(chat, idGroup);
                }
            }
        });
    }

    private void sendMessage(ChatMessage chat, String idGroup){
        //path: ChatMessage
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String messageId = ref.child("ChatMessage").child(idGroup).push().getKey();
        chat.setMessageId(messageId);
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
                            FCMSend.pushNotification(getApplicationContext(), didUserChat, user.getName(), "sent a picture");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else if(chat.getTypeMessage().equals("file")){
            refGroups.setValue("file pdf");
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            FCMSend.pushNotification(getApplicationContext(), didUserChat, user.getName(), "sent a file");
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
                adapter = new ChatMessageAdapter(ChatMessageActivity.this, listChat, new ChatMessageAdapter.OnItemLongClickListener() {
                    @Override public void onItemLongClick(ChatMessage item) {
                        llSendOption.setVisibility(View.GONE);
                        llChatOption.setVisibility(View.VISIBLE);
                        btnDeleteMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snapshot.child(item.getMessageId()).getRef().removeValue();
                                llSendOption.setVisibility(View.VISIBLE);
                                llChatOption.setVisibility(View.GONE);
                            }
                        });
                    }});
                rcvListChat.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
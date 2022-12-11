package com.example.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.R;
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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<ChatMessage> listMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference mStorage;
    private final OnItemLongClickListener listener;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> listMessage, OnItemLongClickListener listener) {
        this.context = context;
        this.listMessage = listMessage;
        this.listener = listener;
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(ChatMessage item);
    }


    @NonNull
    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType == MSG_TYPE_RIGHT) {
            View itemView = inflater.inflate(R.layout.right_message_item, parent, false);
            return new ChatMessageAdapter.ViewHolder(itemView);
        }
        else{
            View itemView = inflater.inflate(R.layout.left_message_item, parent, false);
            return new ChatMessageAdapter.ViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageAdapter.ViewHolder holder, int position) {
        ChatMessage chatItem = listMessage.get(position);
        holder.bind(listMessage.get(position), listener);
        if(chatItem.getTypeMessage().equals("text")){
            holder.tvShowMessage.setVisibility(View.VISIBLE);
            holder.ivShowMessage.setVisibility(View.GONE);
            holder.tvShowMessage.setText(chatItem.getMessage());
        }
        else if(chatItem.getTypeMessage().equals("file")){
            holder.tvShowMessage.setVisibility(View.VISIBLE);
            holder.ivShowMessage.setVisibility(View.GONE);
            holder.tvShowMessage.setText(getFileName(chatItem.getMessage()));
            holder.tvShowMessage.setTypeface(null, Typeface.ITALIC);
            holder.tvShowMessage.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.tvShowMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadFile(chatItem.getMessage());
                }
            });
        }
        else{
            holder.ivShowMessage.setVisibility(View.VISIBLE);
            holder.tvShowMessage.setVisibility(View.GONE);
            FirebaseStorage.getInstance().getReference().child("images/" + chatItem.getMessage())
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(holder.ivShowMessage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(chatItem.getSendBy());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                FirebaseStorage.getInstance().getReference().child("images/" + user.getImage())
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(holder.civShowAvatar);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                holder.tvShowUsername.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        holder.civShowAvatar.setImageResource(R.drawable.cute1);
//        holder.tvShowUsername.setText("mạnh tường");
        holder.tvShowTimeMessage.setText(chatItem.getMessageTime());
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView civShowAvatar;
        public TextView tvShowMessage, tvShowTimeMessage, tvShowUsername;
        public ImageView ivShowMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civShowAvatar = (CircleImageView) itemView.findViewById(R.id.civShowAvatar);
            tvShowMessage = (TextView) itemView.findViewById(R.id.tvShowMessage);
            ivShowMessage = (ImageView) itemView.findViewById(R.id.ivShowMessage);
            tvShowTimeMessage = (TextView) itemView.findViewById(R.id.tvShowTimeMessage);
            tvShowUsername = (TextView) itemView.findViewById(R.id.tvShowUsername);
        }

        public void bind(ChatMessage chatMessage, OnItemLongClickListener listener) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    listener.onItemLongClick(chatMessage);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        if(listMessage.get(position).getSendBy().equals(mAuth.getCurrentUser().getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    public String getFileName(String path) {
        String filename = "";
        int cut = path.lastIndexOf('|');
        if (cut != -1) {
            filename = path.substring(cut + 1);
        }
        return filename;
    }

    private void downloadFile(String path){
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = mStorage.child("files/" + path);
        fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Intent intent = new Intent(Intent.ACTION_VIEW, task.getResult());
                context.startActivity(intent);
            }
        });
    }
}

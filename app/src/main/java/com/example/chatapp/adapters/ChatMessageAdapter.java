package com.example.chatapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<ChatMessage> listMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> listMessage) {
        this.context = context;
        this.listMessage = listMessage;
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
        holder.tvShowMessage.setText(chatItem.getMessage());
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civShowAvatar = (CircleImageView) itemView.findViewById(R.id.civShowAvatar);
            tvShowMessage = (TextView) itemView.findViewById(R.id.tvShowMessage);
            tvShowTimeMessage = (TextView) itemView.findViewById(R.id.tvShowTimeMessage);
            tvShowUsername = (TextView) itemView.findViewById(R.id.tvShowUsername);
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
}

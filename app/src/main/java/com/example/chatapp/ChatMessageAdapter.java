package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<ChatMessage> listMessage;

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
        holder.civShowAvatar.setImageResource(R.drawable.cute1);
        holder.tvShowUsername.setText("Mạnh Tường");
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
        if(listMessage.get(position).isTypeMessage() == true){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}

package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.ViewHolder> {
    private ArrayList<ChatUser> listUser;
    private Context context;

    public ChatUsersAdapter(ArrayList<ChatUser> listUser, Context context) {
        this.listUser = listUser;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_chatuser,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatUser user = listUser.get(position);
        String name = user.getName();
        Picasso.get().load(user.getImage()).into(holder.im_item);
        holder.tv_name.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView im_item;
        TextView tv_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = (CircleImageView)itemView.findViewById(R.id.civImage);
            tv_name = (TextView) itemView.findViewById(R.id.tvName);
        }
    }
}

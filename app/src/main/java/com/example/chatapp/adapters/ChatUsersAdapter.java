package com.example.chatapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.interfaces.RecyclerViewInterface;
import com.example.chatapp.models.Group;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
////////import com.squareup.picasso.Picasso;*////

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.ViewHolder> {
    private ArrayList<Group> listUser;
    private Context context;
    private final RecyclerViewInterface recyclerViewInterface;
//    private DatabaseReference databaseReference;

    public ChatUsersAdapter(ArrayList<Group> listUser, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.listUser = listUser;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_chatuser,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView, recyclerViewInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group user = listUser.get(position);
        String name = user.getName();
        FirebaseStorage.getInstance().getReference().child("images/"+user.getImageId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.im_item);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        holder.tv_name.setText(name);
        holder.tv_message.setText(listUser.get(position).getLastMessage());

        if(!user.isOnline()) {
            holder.im_online.setImageResource(R.color.yellow_circle);
        } else {
            holder.im_online.setImageResource(R.color.green_circle);
        }
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView im_item;
        CircleImageView im_online;
        TextView tv_name;
        TextView tv_message;
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            im_item = (CircleImageView)itemView.findViewById(R.id.civImage);
            im_online = (CircleImageView) itemView.findViewById(R.id.civOnlineCircle);
            tv_name = (TextView) itemView.findViewById(R.id.tvName);
            tv_message = (TextView) itemView.findViewById(R.id.tvMessage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
